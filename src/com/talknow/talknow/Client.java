package com.talknow.talknow;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

import javax.swing.*;

import com.talknow.support.ConfigurationParser;
import com.talknow.support.Message;
import com.talknow.support.Schema;


public class Client extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private Schema schema = Schema.group;
	
	private LinkedList<String> list = null;
	
	private Client THIS;
	private String userid = null;
	private TextField tf2 = null;
	private TextField tf = null; 
    private TextArea ta = null; 
    private Socket s = null;
    private recvThread r = null; 
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private SignInFrame sif;
    private RegisterFrame rf;
    
    private static boolean isregister = false;
    private static boolean issignin = false;
    
    private boolean bConnected = false;

    public Client() {
    	THIS = this;
    	r = new recvThread();
    	
    	JPanel jp = new JPanel();
    	tf2 = new TextField(10);
    	tf = new TextField(40);
    	jp.add(tf2);
    	jp.add(tf);
    	
    	ta = new TextArea();
        ta.setEditable(false);
        this.add(jp, BorderLayout.SOUTH);
        this.add(ta, BorderLayout.NORTH);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });
        tf.addActionListener(new tfListener());
        setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        this.pack();
        this.setLocationRelativeTo(null);
        new Thread(r).start();
        sif = new SignInFrame();
    }

    public void connect() {
        try {
            s = new Socket(ConfigurationParser.getConfIP(), ConfigurationParser.getConfPort());
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
            bConnected = true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "服务器正在努力维护中，谢谢您的理解和支持", "连接错误", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void disconnect() {
        try {
            oos.close();
            ois.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class tfListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String str = tf.getText();
            tf.setText("");
            if(tf2.getText().equals("")) schema = Schema.group;
            else schema = Schema.personal;
            Message m = new Message(schema,userid,tf2.getText(),str);
            try {
                oos.writeObject(m);
                oos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    class recvThread implements Runnable {
        public void run() {
        	connect();
            try {
                while (bConnected) {
                    Message m = null;
					try {
						m = (Message) ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					switch(m.getSchema()) {
						case group:
							ta.append( m.getSender() +":\n" + m.getInfo() + "\n\n");
							break;
						case personal:
							ta.append( m.getSender() + " (私)"+":\n" + m.getInfo() + "\n\n");
							break;
						case response:
							switch(m.getCode()) {
								case 0:
									if(isregister) {
										JOptionPane.showMessageDialog(null, "注册成功", "成功", JOptionPane.INFORMATION_MESSAGE);
										rf.dispose();
									}
									if(issignin) {
										JOptionPane.showMessageDialog(null, "登录成功", "成功", JOptionPane.INFORMATION_MESSAGE);
										sif.dispose();
										THIS.setTitle(userid);
										THIS.setVisible(true);
									}
									break;
								case 1:
									JOptionPane.showMessageDialog(null, m.getInfo(), "错误", JOptionPane.ERROR_MESSAGE);
									break;
								case 2:
									JOptionPane.showMessageDialog(null,m.getInfo(), "错误", JOptionPane.ERROR_MESSAGE);
									break;
							}
							isregister=false;
							issignin=false;
							break;
						case signin:
						case request:
							break;
						case onlinelist:
							list.clear();
							list = m.getList();
							break;
					case register:
						break;
					default:
						break;
                    }
                }
            } catch (SocketException e) {
                System.out.println("退出了");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    class SignInFrame extends JFrame {

    	private static final long serialVersionUID = 1L;
    	
        private JButton jbSignin,jbregister;
        private JPasswordField passField;
        private JTextField nameField;
    	private JLabel jLabel1,jLabel2,jLabel3,jLabel4,jLabel5;
        
    	public SignInFrame() {
            initComponents();
        }
        
        private void initComponents() {

            jLabel1 = new JLabel();
            jLabel2 = new JLabel();
            jLabel3 = new JLabel();
            jLabel4 = new JLabel();
            jLabel5 = new JLabel();
            
            jbregister = new JButton();
            jbSignin = new JButton();
            passField = new JPasswordField();
            nameField = new JTextField();
            
            jLabel1.setFont(new Font("Ink Free", 0, 60));
            jLabel1.setText("TalkNow");

            jLabel2.setText("v1.0");

            jLabel3.setText("账  号:");

            jLabel4.setText("密  码:");

            jLabel5.setFont(new Font("微软雅黑", 0, 24));
            
            jLabel5.setText("客户端");

            jbregister.setText("注  册");
            jbregister.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				register();
    			}
    		});
            
            jbSignin.setText("登  录");
            jbSignin.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				signin();
    			}
    		});
            
            addComponet();

            pack();
            setResizable(false);
            setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setTitle("TalkNow Client");
            setResizable(false);
            setVisible(true);
        }
        
        private void addComponet() {
        	GroupLayout layout = new GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel2))
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nameField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(passField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jbregister, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbSignin, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                    .addGap(100, 100, 100))
                .addGroup(layout.createSequentialGroup()
                    .addGap(68, 68, 68)
                    .addComponent(jLabel1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel5)
                    .addContainerGap(70, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(41, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel5))
                    .addGap(26, 26, 26)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addGap(26, 26, 26)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addGap(30, 30, 30)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jbSignin)
                        .addComponent(jbregister))
                    .addGap(9, 9, 9)
                    .addComponent(jLabel2))
            );
        }
        
        private void register() {
        	rf = new RegisterFrame();
        }
        
        private void signin() {
        	String id = nameField.getText();
        	String pass = new String(passField.getPassword());
        	boolean flag = true;
        	if("".equals(id) && "".equals(pass)) {
        		flag = false;
        		JOptionPane.showMessageDialog(this, "请输入账号与密码", "错误", JOptionPane.ERROR_MESSAGE);
        	}else if("".equals(id)) {
        		flag = false;
	    		JOptionPane.showMessageDialog(this, "请输入账号", "错误", JOptionPane.ERROR_MESSAGE);
	    	}else if("".equals(pass)) {
	    		flag = false;
    	    	JOptionPane.showMessageDialog(this, "请输入密码", "错误", JOptionPane.ERROR_MESSAGE);
        	}else 
        		flag = true;
        	if(flag) {
        		issignin = true;
        		Message m = new Message(Schema.signin, id, "", pass);
        		userid = id;
        		try {
                    oos.writeObject(m);
                    oos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        	}
        }

    }
    
    
    
    class RegisterFrame extends JFrame implements Runnable {

    	private static final long serialVersionUID = 1L;
    	
    	private JButton jb;
        private JCheckBox jcb;
        private JTextField jt1,jt4;
        private JPasswordField jt2,jt3;
    	
    	public RegisterFrame() {
            initComponents();
            new Thread(this).start();
            isregister = true;
        }

        private void initComponents() {

            JLabel jl1 = new JLabel();
            JLabel jl2 = new JLabel();
            JLabel jl3 = new JLabel();
            JLabel jl4 = new JLabel();
            JLabel jl5 = new JLabel();
            
            jb = new JButton();
            jcb = new JCheckBox();
            jt1 = new JTextField();
            jt2 = new JPasswordField();
            jt3 = new JPasswordField();
            jt4 = new JTextField();

            jb.setFont(new Font("微软雅黑", 0, 18));
            jb.setText("注册");
            jb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    jbActionPerformed();
                }
            });
            jb.setEnabled(false);

            jcb.setText("我已阅读并同意用户协议");

            jl1.setFont(new Font("微软雅黑", 0, 24));
            jl1.setText("请认真填写一下信息");
            jl2.setText("账        号:");
            jl3.setText("密        码:");
            jl4.setText("确认密码:");
            jl5.setText("昵        称:");

            GroupLayout layout = new GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jl1)
                    .addGap(82, 82, 82))
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(94, 94, 94)
                                .addComponent(jcb))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addComponent(jb, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(jl2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jt1, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jl5)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jt4, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jl4)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jt3, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jl3)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jt2, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(56, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jl1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jl2)
                        .addComponent(jt1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jl3)
                        .addComponent(jt2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jl4)
                        .addComponent(jt3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jl5)
                        .addComponent(jt4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addComponent(jcb)
                    .addGap(18, 18, 18)
                    .addComponent(jb)
                    .addGap(25, 25, 25))
            );
            pack();
            setResizable(false);
            setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setTitle("TalkNow 用户注册");
            setResizable(false);
            setVisible(true);
        }

        private void jbActionPerformed() {
        	if(checkTF1() && checkTF3()) {
        		String id = jt1.getText();
        		String pass = new String(jt2.getPassword());
        		String name = jt4.getText();
        		Message m = new Message(Schema.register, id, name, pass);
        		try {
        			oos.writeObject(m);
        			oos.flush();
        		}catch (Exception e) {
        			e.getStackTrace();
				}
        		isregister = true;
        		userid = id;
        	}
        }
        
        private boolean checkTF1() {
        	if(jt1.getText().length() == 10) {
        		return true;
        	}
        	return false;
        }
        
        private boolean checkTF3() {
        	String str1 = new String(jt2.getPassword());
        	String str2 = new String(jt3.getPassword());
        	if(str1.equals(str2)) {
        		return true;
        	}else {
        		JOptionPane.showMessageDialog(this, "确认密码与密码不一致", "警告", JOptionPane.WARNING_MESSAGE);
        		return false;
        	}
        }
        
        
        boolean runningflag = true;
        @Override
        public void run() {
        	while(runningflag) {
        		if(!"".equals(jt1.getText()))
            		if(!"".equals(new String(jt2.getPassword())))
            			if(!"".equals(new String(jt3.getPassword())))
            				if(!"".equals(jt4.getText()))
        						if(jcb.isSelected()) {
        							jb.setEnabled(true);
        							runningflag = false;
        						}
        		try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        }

    }
}