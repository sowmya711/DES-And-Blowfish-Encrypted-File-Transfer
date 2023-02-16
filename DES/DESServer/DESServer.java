//DESServer.java
import javax.crypto.Cipher;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Base64;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.security.*;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.PublicKey;
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
import java.security.spec.X509EncodedKeySpec;
public class DESServer {
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    static String deckey;
    public static void main(String[] args) throws IOException {
    int fileId = 0;
    JFrame jFrame = new JFrame("DES Server");
    jFrame.setSize(700, 700);
    jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
    JScrollPane jScrollPane = new JScrollPane(jPanel);
    jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    JLabel jlTitle = new JLabel("DES File Receiver");
    jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
    jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
    jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    jFrame.add(jlTitle);
    jFrame.add(jScrollPane);
    jFrame.setVisible(true);
    ServerSocket serverSocket = new ServerSocket(1234);
    try{    
       KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
       kpg.initialize(2048);
       KeyPair kp = kpg.generateKeyPair();
       Key pubKey = kp.getPublic();
       Key privKey=kp.getPrivate();
       Socket s = serverSocket.accept();
       String pubkey = Base64.getEncoder().encodeToString(pubKey.getEncoded());
       DataOutputStream objectOutputStream = new DataOutputStream(
       s.getOutputStream());
       objectOutputStream.writeUTF(pubkey);
       System.out.println("Public key "+pubkey);
       objectOutputStream.flush();
       DataInputStream dis=new DataInputStream(s.getInputStream());  
            String enckey=dis.readUTF();
            System.out.println(enckey);
            byte[] encryptedBytes =  Base64.getDecoder().decode(enckey);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE,privKey);
            byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
           deckey= new String(decryptedMessage,StandardCharsets.UTF_8);
           System.out.println(deckey);
    }
       catch(Exception ee){
               ee.printStackTrace();
       }
    while (true) {
        try {
    
            Socket socket = serverSocket.accept();
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            int fileNameLength = dataInputStream.readInt();
            if (fileNameLength > 0) {
                byte[] fileNameBytes = new byte[fileNameLength];
                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                String fileName = new String(fileNameBytes);
                int fileContentLength = dataInputStream.readInt();
                if (fileContentLength > 0) {
                    byte[] fileContentBytes = new byte[fileContentLength];
                    dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                    JPanel jpFileRow = new JPanel();
                    jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.X_AXIS));
                    JLabel jlFileName = new JLabel(fileName);
                    jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                    jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
                    if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                        jpFileRow.setName((String.valueOf(fileId)));
                        jpFileRow.addMouseListener(getMyMouseListener());
                        jpFileRow.add(jlFileName);
                        jPanel.add(jpFileRow);
                        jFrame.validate();
                       } else {
                          
                           jpFileRow.setName((String.valueOf(fileId)));
                           jpFileRow.addMouseListener(getMyMouseListener());
                           jpFileRow.add(jlFileName);
                           jPanel.add(jpFileRow);
                           jFrame.validate();
                       }
                       myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                       fileId++;
                   }
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
                          
       }
       public static void write(InputStream in, OutputStream out) throws IOException {
           byte[] buffer = new byte[64];
           int numOfBytesRead;
           while ((numOfBytesRead = in.read(buffer)) != -1) {
               out.write(buffer, 0, numOfBytesRead);
             }
             out.close();
             in.close();
       
         }
         public static void decrypt(String key, File in, File out)  throws InvalidKeyException,InvalidKeySpecException,NoSuchAlgorithmException,NoSuchPaddingException,IOException
         {
            FileInputStream fin = new FileInputStream(in);
            FileOutputStream fout = new FileOutputStream(out);
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = skf.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, SecureRandom.getInstance("SHA1PRNG"));
            CipherInputStream cos = new CipherInputStream(fin, cipher);
            System.out.println(cos);
            write(cos, fout);
        
     }
     public static String getFileExtension(String fileName) {
       
         int i = fileName.lastIndexOf('.');
         if (i > 0) {
             return fileName.substring(i + 1);
            
         } else {
             return "No extension found.";
         }
         
     }
     public static MouseListener getMyMouseListener() {
         return new MouseListener() {
             @Override
             public void mouseClicked(MouseEvent e) {
                 
                 JPanel jPanel = (JPanel) e.getSource();
                 int fileId = Integer.parseInt(jPanel.getName());
                 for (MyFile myFile : myFiles) {
                     if (myFile.getId() == fileId) {
                         JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                         jfPreview.setVisible(true);
                
              
                     }
                 }
             }
   
             @Override
             public void mousePressed(MouseEvent e) {
             }
             @Override
             public void mouseReleased(MouseEvent e) {
             }
             @Override
             public void mouseEntered(MouseEvent e) {
             }
             @Override
             public void mouseExited(MouseEvent e) {
             }
         };
     }
   
     public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {
   
         System.out.println(new String(fileData));
         JFrame jFrame = new JFrame(" File Downloader");
         
         JPanel jPanel = new JPanel();
         jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
         JLabel jlTitle = new JLabel("File Downloader");
         JLabel jlPrompt = new JLabel("Are you sure you want to download " + fileName + "?");
         jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
         jlTitle.setFont(new Font("Times New Roman", Font.BOLD, 25));
         jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
         
         jlPrompt.setFont(new Font("Times New Roman", Font.BOLD, 25));
         jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
         jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
         JButton jbYes = new JButton("Yes");
         jbYes.setPreferredSize(new Dimension(120, 75));
         jbYes.setFont(new Font("Times New Roman", Font.BOLD, 25));
         JButton jbNo = new JButton("No");
         jbNo.setPreferredSize(new Dimension(120, 75));
         jbNo.setFont(new Font("Times New Roman", Font.BOLD, 25));
         JLabel jlFileContent = new JLabel();
         jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);
         JPanel jpButtons = new JPanel();
         jFrame.setSize(700, 700);
         jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
         jpButtons.add(jbYes);
         jpButtons.add(jbNo);
         if (fileExtension.equalsIgnoreCase("txt")) {
             jlFileContent.setText("<html>" + new String(fileData) + "</html>");
           } else {
               jlFileContent.setIcon(new ImageIcon(fileData));
           }
         
         jbYes.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 File fileToDownload = new File(fileName);
                 
                     try {
                         FileOutputStream fileOutputStream1 = new FileOutputStream(fileToDownload);
                         fileOutputStream1.write(fileData);
                         fileOutputStream1.close();
                         jFrame.dispose();
                         File encrypted = new File("DESFile");
                         File plaintext =new File("original");
                         try{
                 
                             decrypt(deckey,encrypted,plaintext); 
                           }
                           catch(Exception ex2)
                           {
                               ex2.printStackTrace();
                           }
                       } catch (IOException ex) {
                           ex.printStackTrace();
                       }
                   }
               });
               jbNo.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       
                       jFrame.dispose();
                   }
               });
               jPanel.add(jlTitle);
               jPanel.add(jlPrompt);
               jPanel.add(jlFileContent);
               jPanel.add(jpButtons);
               jFrame.add(jPanel);
               return jFrame;
             }
         }
               
            





      
    


                     
                          
                         
                         
    
          
    
                
               
          
         
               
                        
                    
                  
          
    