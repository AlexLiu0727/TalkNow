package com.talknow.talknow;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Vector;

import com.talknow.database.Record;
import com.talknow.support.ConfigurationParser;
import com.talknow.support.Message;
import com.talknow.support.Node;

public class Servers {
	
    private boolean started = false;
    private ServerSocket ss = null;
    
    private ArrayList<ChatClient> ca = null; //保存客户端线程类 
    
    private Vector<com.talknow.support.Node> cv = null;//保存最近登录的客户，size == 10 ;
    
    public Servers() {
    	ca = new ArrayList<>();
    	cv = new Vector<>();
        try {
            ss = new ServerSocket(ConfigurationParser.getConfPort()); //建立服务端对象 
            started = true;
        } catch (BindException e) {
            System.out.println("端口使用中");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (started) {
                Socket s = ss.accept(); //接收客户端 
                ChatClient c = new ChatClient(s);
                System.out.println("客戶端接收成功");
                new Thread(c).start(); //启动线程 
                ca.add(c); //添加线程类 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Record LRU(Record r) {
    	boolean b = false;
    	Node n = null;
		for(int i = 0 ; i < cv.size() ; i++){
			if(cv.get(i).getId().equals(r.getId())){
				n = cv.remove(i);
				b = true;
				break;
			}
		}
		if(b == true) {
			cv.add(n);
		}else {
			com.talknow.database.DAO p = com.talknow.database.DatabaseFactory.getNewProxy();
			Record e = p.searchById(r.getId());
			p.invalidate();
			if(e!=null) {
				n = new Node(e);
				if(cv.size()>=10) {
					cv.remove(0);
				}
				cv.add(n);
			}
		}
		return n;
	}
    
//    private void showLRU() {
//    	for(int i=0;i<cv.size();i++) {
//    		System.out.print(cv.get(i).getId()+" ");
//    	}
//    }
    
    class ChatClient implements Runnable { //建立客户端线程接收，发送数据 
    	
        private Socket socket;
        private boolean bConnected = false;
 
        private String id = null;
        private ObjectOutputStream oos = null;
        private ObjectInputStream ois = null;
        
        public ChatClient(Socket s) {
            this.socket = s;
            try {
            	oos = new ObjectOutputStream(s.getOutputStream());
            	ois = new ObjectInputStream(s.getInputStream());
            	bConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getID() {
        	return id;
        }
        
        public void send(Message m) {
            try {
            	oos.writeObject(m);
            	oos.flush();
            } catch (SocketException e) {
                System.out.println("對方退出了");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
		public void run() {
            try {
                while (bConnected) {
                	Message m = null;
					try {
						m = (Message) ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
	bo:				switch(m.getSchema()) {
					case group:
	                    for (int i = 0; i < ca.size(); i++) {
	                    	ca.get(i).send(m);
	                    }
	                    break;
					case personal:
						boolean issend = false;
						for (int i = 0; i < ca.size(); i++) {
							if(ca.get(i).getID().equals(m.getGetter())) {
								ca.get(i).send(m);
								issend = true;
								break;
							}
	                    }
						if(!issend) {
							Message re = new Message("该用户不存在或当前未在线", 2);
							for (int i = 0; i < ca.size(); i++) {
								if(ca.get(i).getID().equals(m.getSender())) {
									ca.get(i).send(re);
									break;
								}
		                    }
						}
						break;
					case signin:
						String id = m.getSender();
						String pass = m.getInfo();
						for (int i = 0; i < ca.size(); i++) {
							if(id.equals(ca.get(i).getID())) {
								send(new Message("该用户当前在线", 2));
								break bo;
							}
						}
						com.talknow.database.Record r = LRU(new com.talknow.database.Record(id, pass, "", ""));
						if(r!=null) {
							if(r.getPasscode().equals(pass)) {
								send(new Message("登录成功", 0));
								this.id = id;
							} else {
								send(new Message("登录失败", 1));
							}
						} else {
							send(new Message("登录失败", 1));
						}
//						showLRU();
						break;
					case response://客户端没有创建response消息的能力，所以不做判断
					case onlinelist://客户端不能创建在线列表，所以不做判断
					case request:break;//////改变当前状态用，预留位
					case register:
						com.talknow.database.DAO pro = com.talknow.database.DatabaseFactory.getNewProxy();
						if(pro.searchById(m.getSender()).equals(null)) {
							send(new Message("注册失败，该id存在",1));
						}else {
							int i = pro.add(new Record(m.getSender(),m.getInfo(),m.getGetter(),null));
							switch(i) {
								case 0: send(new Message("注册成功", 0));break;
								case 1: send(new Message("注册失败,请更换您的id", 1));break;
								case 2: send(new Message("数据库维护中，请重试", 2));break;
							}
						}
						pro.invalidate();
						break;
					default:break;
					}
				}
            } catch (EOFException e) {
                System.out.println("客戶端退出了");
                for(int i = 0 ; i < ca.size() ; i++) {
                	if(ca.get(i).getID().equals(this.id)) {
                		ca.remove(i);
                		break;
                	}
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (ois != null)
                    if (socket != null)
                        try {
                            ois.close();
                            oos.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
            }
        }
    }
    
    
}
