package cn.dang.ping;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class Pinger {

	private JFrame frame;
	private JTextField txtName;
	private JProgressBar processBar;
	private static Properties properties;
	volatile boolean flag=false;
	private Set<Object> ips;
	volatile int num;
	static {

		properties = new Properties();
		try {
			String path2 = System.getProperty("user.dir");
			path2.replaceAll("\\\\", "\\\\\\\\");
			BufferedReader br = null; 
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(
							new File(path2+"\\src\\cn\\dang\\ping\\ip.properties")),"GBK"));
			properties.load(br);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	/**
	 *
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Pinger window = new Pinger();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void start(Integer count) {
		ips = properties.keySet();
		 num = ips.size();
		System.out.println("开始测试");
		for (Object ip : ips) {
			try {
				ping(String.valueOf(ip), count);
				num--;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("测试完成");
		flag=true;

	}

	private void ping(String ip, Integer count) throws IOException {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

		String path2 = System.getProperty("user.dir");
		path2.replaceAll("\\\\", "\\\\\\\\");
		Process pro = Runtime.getRuntime().exec("ping " + ip + " -n " + count);
		BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		String line = null;
		FileWriter fw = new FileWriter(path2+"\\src\\cn\\dang\\ping\\"
				+ properties.get(ip) + format.format(new Date()) + ".txt", true);
		BufferedWriter bw = new BufferedWriter(fw);

		while ((line = br.readLine()) != null) {

			bw.write(new String(line.getBytes(), "GBK"));
			bw.newLine();
		}

		bw.flush();
		bw.close();
		br.close();

	}

	/**
	 * 10
	 *
	 * Create the application.
	 */
	public Pinger() {
		try {
			initialize(properties);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Initialize the contents of the frame.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void initialize(Properties map) throws UnsupportedEncodingException {
		frame = new JFrame();
		frame.setBounds(800, 200, 496, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
        processBar=new JProgressBar();
        processBar.setStringPainted(true);// 设置进度条上的字符串显示，false则不能显示
		processBar.setBackground(Color.darkGray);
		txtName = new JTextField();
		txtName.setBounds(90, 410, 93, 21);
		frame.getContentPane().add(txtName);
		txtName.setColumns(10);

		JButton btnSubmit = new JButton("开始");
		JButton btnStop = new JButton("退出");
        ActionListener ajj=new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String ae = e.getActionCommand();
				if (ae.equals("开始")) {
					System.out.println("测试");
					Runnable runnable= new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							while (!flag) {
								Integer count = Integer.valueOf(txtName.getText());
								if(count>=1 && count<=10000){
									start(count);
								}else{
									System.out.println("请输入1~10000之内的数");
									flag=true;
								}
							}
							
							
						}
					};
					Thread thread=new Thread(runnable);
					thread.start();
					
					//进度条
					Runnable runnable1= new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							processBar.setValue(0);							
						while (!flag) {
							if (num!=0) {
								if(num==(ips.size()/2)) {
									
									processBar.setValue(50);
								}
							
								if(num==1) {
									
									processBar.setValue(100);
								}
							}else {
								flag=true;
							}
							
						}
							processBar.setString("完成");
							//frame.dispose();
						}
						
					};
					processBar.setBounds(110, 500, 250, 15);
					frame.getContentPane().add(processBar);
					Thread thread1=new Thread(runnable1);
					thread1.start();
					
					
					//结束
				}
				if(ae=="退出"){
					System.out.println("结束");
					flag=true;
					System.exit(1);
				}
				
			}
		};
/*		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while (!flag) {
					new Thread(new Runnable() {
						public void run() {
				
							System.out.println("1");
						}
					}).start();
					
				}
			
				
				Integer count = Integer.valueOf(txtName.getText());
				start(count);

			}

		});*/
		btnSubmit.setBounds(240, 408, 93, 23);
		frame.getContentPane().add(btnSubmit);
		btnSubmit.addActionListener(ajj);
		btnStop.setBounds(350, 408, 93, 23);
		frame.getContentPane().add(btnStop);
		btnStop.addActionListener(ajj);
		
		
		int height = 20;
		int width = 20;

		Set<Object> keys = map.keySet();
		for (Object ip : keys) {
			JLabel ipAndAddr = new JLabel(String.format("%s:%s", String.valueOf(map.get(ip)), String.valueOf(ip)));
			ipAndAddr.setBounds(width, height, 500, 20);
			frame.getContentPane().add(ipAndAddr);
			height = height + 23;
		}

		JLabel lblName = new JLabel("Ping 次数:");
		lblName.setBounds(20, 410, 70, 15);
		frame.getContentPane().add(lblName);
		

	}
	


}