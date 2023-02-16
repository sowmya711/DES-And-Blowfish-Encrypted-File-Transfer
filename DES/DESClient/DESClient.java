//DESClient.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.*;
import java.util.Base64;
import java.nio.charset.StandardCharsets.*;
import java.net.Socket;
import java.security.PublicKey;
import java.net.ServerSocket;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import java.security.SecureRandom;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DESKeySpec;

public class DESClient {
    static Socket s = null;
    static PublicKey pubKey = null;
    static String enckey;
    public static void encrypt(String key, int cipherMode, File in, File out)
           throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException,
           IOException {
           FileInputStream fin = new FileInputStream(in);
           FileOutputStream fout = new FileOutputStream(out);
           DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
           SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
           SecretKey secretKey = skf.generateSecret(desKeySpec);
           Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            if (cipherMode == Cipher.ENCRYPT_MODE) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, SecureRandom.getInstance("SHA1PRNG"));
            CipherInputStream cis = new CipherInputStream(fin, cipher);
            write(cis, fout);

        }
    }

    private static void write(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[64];
        int numOfBytesRead;
        while ((numOfBytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, numOfBytesRead);

        }
        out.close();
        in.close();

    }

    public static void main(String[] args) {
    try{
        s=new Socket("localhost",1234);   
        DataInputStream dis=new DataInputStream(s.getInputStream());  
        String enckey=dis.readUTF();
        try {
            byte[] publicBytes = Base64.getDecoder().decode(enckey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(keySpec);
            System.out.println("Public key received: "+pubKey);
            } catch (Exception ex) {
            ex.printStackTrace();
            }
        }
        catch(Exception ey){
          ey.printStackTrace();
        }
             
        final File[] fileToSend = new File[2];
        JFrame jFrame = new JFrame("DES Client");
        JLabel jlTitle = new JLabel(" File Sender");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel jfname = new JLabel("Send key and file");
        jfname.setFont(new Font("Arial", Font.BOLD, 20));
        jfname.setBorder(new EmptyBorder(50, 0, 0, 0));
        jfname.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel jpb = new JPanel();
        jpb.setBorder(new EmptyBorder(75, 0, 10, 0));
        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(120, 75));
        JButton jbSendKey = new JButton("Send Key");
        jbSendKey.setPreferredSize(new Dimension(120, 75));
        JButton jbChooseFile = new JButton("Choose File");
        jbChooseFile.setPreferredSize(new Dimension(120, 75));
        JTextField jt=new JTextField("Enter key here");
        jt.setBounds(400,400,100,50);
        jt.setPreferredSize(new Dimension(120,70));
        jFrame.setSize(750, 750);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jpb.add(jbSendKey);
        jpb.add(jbSendFile);
        jpb.add(jbChooseFile);
        jpb.add(jt);
        
        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                if (e.getSource() == jbChooseFile) {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setDialogTitle("Choose a file to send.");
                    if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        fileToSend[0] = jFileChooser.getSelectedFile();
                        jfname.setText("The file you want to send is: " + fileToSend[0].getName());
                        
                      }
                    }
                    
                }
            });
            
            jbSendFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    if (fileToSend[0] == null) {
                        jfname.setText("Please choose a file to send first!");
                       
                    } else {
                        try {
                            File plaintext = new File(fileToSend[0].getAbsolutePath());
                            File encrypted = new File("DESFile");
                            Socket socket = new Socket("localhost", 1234);
                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            try {
                           
                                encrypt(jt.getText(), Cipher.ENCRYPT_MODE, plaintext, encrypted);
                                FileInputStream fileInputStream = new FileInputStream(encrypted.getAbsolutePath());
                                String filename = encrypted.getName();
                                byte[] fileNameBytes = filename.getBytes();
                                byte[] fileBytes = new byte[(int) encrypted.length()];
                                fileInputStream.read(fileBytes);
                                dataOutputStream.writeInt(fileNameBytes.length);
                                dataOutputStream.write(fileNameBytes);
                                dataOutputStream.writeInt(fileBytes.length);
                                dataOutputStream.write(fileBytes);
                            } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
                                    | NoSuchPaddingException | IOException e1) {
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
                        try{
                                String keystr=jt.getText();
                                byte[] messageToBytes = keystr.getBytes();
                                Cipher cipher = Cipher.getInstance("RSA");
                                cipher.init(Cipher.ENCRYPT_MODE,pubKey);
                                byte[] encryptedBytes = cipher.doFinal(messageToBytes);
                                String sendkey=Base64.getEncoder().encodeToString(encryptedBytes);
                                System.out.println("RSA Encrypted key"+sendkey);
                                DataOutputStream dous = new DataOutputStream(
                                    s.getOutputStream());
                                    dous.writeUTF(sendkey);
                                    s.close();
                                }
                                
                             catch (Exception ex2) {
                                 ex2.printStackTrace();
                             }
                            }
                           
                        }
                        ); 
                        jFrame.add(jlTitle);
                        jFrame.add(jfname);
                        jFrame.add(jpb);
                        jFrame.setVisible(true);
                    }
                
                }
                        
                                       
            
                            
                            
                            
                       
                           
                          
    
                                
    
                               
                               
                             
              
                   
                       
                        
       
     
        
       
        
 
       
       
       
       

          
        
         


     
       
   
      
      
       
      
     
        
        
      


        
