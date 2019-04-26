package com.talknow.surface;

import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;


public class ServerMainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
		private JButton jbOFF;
		private JButton jbON;
	    private JLabel jl1_x;
	    private JLabel jl2_x;
	    private JLabel jl3_x;
	    
	public ServerMainFrame() {
        initComponents();
    }

    private void initComponents() {

    	JLabel jLabelTitle = new JLabel();
    	JLabel jl1 = new JLabel();
    	JLabel jl2 = new JLabel();
    	JLabel jl3 = new JLabel();
    	
        jbON = new JButton();
        jbOFF = new JButton();
        jl1_x = new JLabel();
        jl2_x = new JLabel();
        jl3_x = new JLabel();

        jLabelTitle.setFont(new Font("微软雅黑", 0, 24));
        jLabelTitle.setText("TalkNow服务器 设置器");

        jbON.setText("开启服务");
        jbON.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbONActionPerformed(evt);
            }
        });

        jbOFF.setText("关闭服务");
        jbOFF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jbOFFActionPerformed(evt);
            }
        });

        jl1.setText("当前状态:");

        jl1_x.setText("Closed");

        jl2.setText("使用端口:");

        jl2_x.setText(""+com.talknow.support.ConfigurationParser.getConfPort());

        jl3.setText("本机IP:");
        
        try {
			jl3_x.setText(InetAddress.getLocalHost().getHostAddress().toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jbON)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbOFF))
                    .addComponent(jLabelTitle)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jl1)
                            .addComponent(jl2)
                            .addComponent(jl3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jl3_x, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jl2_x, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jl1_x, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabelTitle)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jl1)
                    .addComponent(jl1_x))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jl2)
                    .addComponent(jl2_x))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jl3)
                    .addComponent(jl3_x))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jbON)
                    .addComponent(jbOFF))
                .addGap(27, 27, 27))
        );
        pack();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 350));
        int y = Toolkit.getDefaultToolkit().getScreenSize().height-340-30;//400窗口高度，30菜单栏
        setLocation(0, y);
        setIconImage(Toolkit.getDefaultToolkit().getImage("iconS.png"));
        setTitle("TalkNow Server");
        setResizable(false);
        setVisible(true);
    }
    
    /**开启服务*/
    private void jbONActionPerformed(ActionEvent evt) {
    	jl1_x.setText("Running");
    	jl1_x.setForeground(new Color(0, 200, 0));
    	new Thread() {
    		public void run() {
    			new com.talknow.talknow.Servers();
    		}
    	}.start();
    }
    
    /**关闭服务*/
    private void jbOFFActionPerformed(ActionEvent evt) {
    	
    	jl1_x.setText("Stoped");
        jl1_x.setForeground(new Color(220, 0, 0));
        this.setVisible(true);
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.exit(0);
    }
}
