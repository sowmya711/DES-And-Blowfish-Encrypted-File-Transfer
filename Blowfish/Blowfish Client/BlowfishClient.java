//BlowfishClient.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.Charset;
import java.net.Socket;
import java.security.*;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

import java.security.SecureRandom;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DESKeySpec;
public class BlowfishClient {
    private static final String ALGORITHM = "Blowfish";
    static File[] fileToSend = new File[2];
    private static void doC(int cipherMode, File inputFile,
			File outputFile) throws Exception {

                String keyString;
                File fileips = new File(fileToSend[1].getAbsolutePath());
                BufferedReader br= new BufferedReader(new FileReader(fileips));
                keyString = br.readLine().trim();
                System.out.println(keyString);
                Key secretKey = new SecretKeySpec(keyString.getBytes(), ALGORITHM);
		        Cipher cipher = Cipher.getInstance(ALGORITHM);
		        cipher.init(cipherMode, secretKey);
                 FileInputStream inputStream = new FileInputStream(inputFile);
		        byte[] inputBytes = new byte[(int) inputFile.length()];
		        inputStream.read(inputBytes);
                byte[] outputBytes = cipher.doFinal(inputBytes);
                FileOutputStream outputStream = new FileOutputStream(outputFile);
		        outputStream.write(outputBytes);
                inputStream.close();
		        outputStream.close();
    }

    public static void encrypt( File in, File out)
    throws Exception
    {
           {
                
                doC(Cipher.ENCRYPT_MODE, in, out);
		        System.out.println("File encrypted successfully!");
            }
    }
 public static void main(String[] args) {

       
        JFrame jFrame = new JFrame("Client");
        JLabel jlTitle = new JLabel("Secure File Sender Blowfish");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        JLabel jlFileName = new JLabel("Choose a key file to send first.");
        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jlFileName.setBorder(new EmptyBorder(50, 0, 0, 0));
        JPanel jpb = new JPanel();
        jpb.setBorder(new EmptyBorder(75, 0, 10, 0));
        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(100, 75));
        JButton jbSendKey = new JButton("Send Key");
        jbSendKey.setPreferredSize(new Dimension(120, 75));
        JButton jbChooseFile = new JButton("Choose File");
        jbChooseFile.setPreferredSize(new Dimension(120, 75));
        JButton jbChooseKey = new JButton("Choose Key");
        jbChooseKey.setPreferredSize(new Dimension(120, 75));
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jFrame.setSize(750, 750);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jpb.add(jbSendKey);
        jpb.add(jbSendFile);
        jpb.add(jbChooseFile);
        jpb.add(jbChooseKey);
        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                if (e.getSource() == jbChooseFile) {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setDialogTitle("Choose a file to send.");
                    if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                       fileToSend[0] = jFileChooser.getSelectedFile();
                       jlFileName.setText("The file you want to send is: " + fileToSend[0].getName());

                    }
                }

            }
        });

        jbChooseKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             if (e.getSource() == jbChooseKey) {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setDialogTitle("Choose a key file to send.");
                    if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        fileToSend[1] = jFileChooser.getSelectedFile();
                        jlFileName.setText("The key file  you want to send is: " + fileToSend[1].getName());
                    }
                }

            }
        });

       
        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                if (fileToSend[0] == null) {
                    jlFileName.setText("Please choose a file to send first!");
                   
                } else {
                    try {
                        File plaintext = new File(fileToSend[0].getAbsolutePath());
                        File encrypted = new File("encrypteddata");
                        Socket socket = new Socket("localhost", 1234);
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        try {
                       
                            encrypt(plaintext,encrypted);
                            FileInputStream fileInputStream = new FileInputStream(encrypted.getAbsolutePath());
                            String filename = encrypted.getName();
                            byte[] fileNameBytes = filename.getBytes();
                            byte[] fileBytes = new byte[(int) encrypted.length()];
                            fileInputStream.read(fileBytes);
                            dataOutputStream.writeInt(fileNameBytes.length);
                            dataOutputStream.write(fileNameBytes);
                            dataOutputStream.writeInt(fileBytes.length);
                            dataOutputStream.write(fileBytes);
                            
                        } catch (Exception e1) {
                          
                            e1.printStackTrace();
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        
                    }
               }
           }
        });
                        
                                                
                            
    jbSendKey.addActionListener(new ActionListener()
    {
       @Override
       public void actionPerformed(ActionEvent ae) {

           if (fileToSend[1] == null) {
               jlFileName.setText("Please choose a file to send first!");
               
           } else {

               try{
                   FileInputStream fileInputStream = new FileInputStream(fileToSend[1].getAbsolutePath());



                   Socket socket = new Socket("localhost", 1234);
                 
                   DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                  
                   String fileName = fileToSend[1].getName();
                   
                   byte[] fileNameBytes = fileName.getBytes();
      
                   byte[] fileBytes = new byte[(int)fileToSend[1].length()];
       
                   fileInputStream.read(fileBytes);
                 
                   dataOutputStream.writeInt(fileNameBytes.length);
                  
                   dataOutputStream.write(fileNameBytes);
                   
                   dataOutputStream.writeInt(fileBytes.length);
                  
                   dataOutputStream.write(fileBytes);
               } catch (IOException ex) {
                   ex.printStackTrace();
               }

               
       }
   }
   }); 
    jFrame.add(jlTitle);
    jFrame.add(jlFileName);
    jFrame.add(jpb);
    jFrame.setVisible(true);
}

}
                           
                           
                           

                        
                       
   
                        
                            

                            
