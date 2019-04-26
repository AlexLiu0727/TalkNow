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
    
    private ArrayList<ChatClient> ca = null; //����ͻ����߳��� 
    
    private Vector<com.talknow.support.Node> cv = null;//���������¼�Ŀͻ���size == 10 ;
    
    public Servers() {
    	ca = new ArrayList<>();
    	cv = new Vector<>();
        try {
            ss = new ServerSocket(ConfigurationParser.getConfPort()); //��������˶��� 
            started = true;
        } catch (BindException e) {
            System.out.println("�˿�ʹ����");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (started) {
                Socket s = ss.accept(); //���տͻ��� 
                ChatClient c = new ChatClient(s);
                System.out.println("�͑��˽��ճɹ�");
                new Thread(c).start(); //�����߳� 
                ca.add(c); //����߳��� 
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
    
    class ChatClient implements Runnable { //�����ͻ����߳̽��գ��������� 
    	
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
                System.out.println("�����˳���");
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
							Message re = new Message("���û������ڻ�ǰδ����", 2);
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
								send(new Message("���û���ǰ����", 2));
								break bo;
							}
						}
						com.talknow.database.Record r = LRU(new com.talknow.database.Record(id, pass, "", ""));
						if(r!=null) {
							if(r.getPasscode().equals(pass)) {
								send(new Message("��¼�ɹ�", 0));
								this.id = id;
							} else {
								send(new Message("��¼ʧ��", 1));
							}
						} else {
							send(new Message("��¼ʧ��", 1));
						}
//						showLRU();
						break;
					case response://�ͻ���û�д���response��Ϣ�����������Բ����ж�
					case onlinelist://�ͻ��˲��ܴ��������б����Բ����ж�
					case request:break;//////�ı䵱ǰ״̬�ã�Ԥ��λ
					case register:
						com.talknow.database.DAO pro = com.talknow.database.DatabaseFactory.getNewProxy();
						if(pro.searchById(m.getSender()).equals(null)) {
							send(new Message("ע��ʧ�ܣ���id����",1));
						}else {
							int i = pro.add(new Record(m.getSender(),m.getInfo(),m.getGetter(),null));
							switch(i) {
								case 0: send(new Message("ע��ɹ�", 0));break;
								case 1: send(new Message("ע��ʧ��,���������id", 1));break;
								case 2: send(new Message("���ݿ�ά���У�������", 2));break;
							}
						}
						pro.invalidate();
						break;
					default:break;
					}
				}
            } catch (EOFException e) {
                System.out.println("�͑����˳���");
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
