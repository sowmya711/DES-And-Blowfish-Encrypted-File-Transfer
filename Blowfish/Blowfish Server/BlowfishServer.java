//BlowfishServer.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.nio.charset.Charset;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.Socket;
import java.security.*;
import javax.crypto.NoSuchPaddingException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;
public class BlowfishServer {
    private static final String ALGORITHM = "Blowfish";
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        int fileId = 0;
        JFrame jFrame = new JFrame("Blowfish Server");
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        JScrollPane jsp = new JScrollPane(jPanel);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JLabel jlTitle = new JLabel("Blowfish File Receiver");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jFrame.setSize(700, 700);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(jlTitle);
        jFrame.add(jsp);
        jFrame.setVisible(true);
        ServerSocket serverSocket = new ServerSocket(1234);
        while (true) {
            try {
                
                Socket socket = serverSocket.accept();
                DataInputStream dis= new DataInputStream(socket.getInputStream());
                int fileNameLength = dis.readInt();
                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dis.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);
                    int fileContentLength = dis.readInt();
                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
        
                        dis.readFully(fileContentBytes, 0, fileContentBytes.length);
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
        
        private static void doC(String keyString, int cipherMode, File inputFile,
            File outputFile) throws Exception {
        
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
    public static void decrypt(String st,int cipherMode,File in, File out)  throws Exception
        
        {
        doC(st,cipherMode, in, out);
        System.out.println("File decrypted successfully!");
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
        JFrame jFrame = new JFrame("File Downloader");
        jFrame.setSize(400, 400);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        JLabel jlTitle = new JLabel("Blowfish File Downloader");
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        JLabel jlPrompt = new JLabel("Are you sure you want to download " + fileName + "?");
        jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
        jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150, 75));
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));
        JButton jbNo = new JButton("No");
        jbNo.setPreferredSize(new Dimension(150, 75));
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel jlFileContent = new JLabel();
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel jpButtons = new JPanel();
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
              if(fileName.equals("keyfile.txt"))
              { 
                  try {
                      FileOutputStream fileOutputStream1 = new FileOutputStream(fileToDownload);
                      fileOutputStream1.write(fileData);
                      fileOutputStream1.close();
                      jFrame.dispose();
                      File kf=new File("keyfile.txt");
                      BufferedReader br= new BufferedReader(new FileReader(kf));
                      String st;
                      st=br.readLine().trim();
                      System.out.println(st);
                      File encrypted = new File("encrypteddata");
                      File plaintext =new File("original");
                      try{
              
                          decrypt(st,Cipher.DECRYPT_MODE,encrypted,plaintext); 
                        }
                        catch(Exception ex2)
                        {
                            ex2.printStackTrace();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                        fileOutputStream.write(fileData);
                        fileOutputStream.close();
                        jFrame.dispose();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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
                
              
              
              
                  
          
          
          
          
          
          
          
          
            
             
          
            
                          
              
                      
                      
                          
                        
                         
                         
              
              
                      
              
              
              
              
              
          
         
              
          
                
                  
                      
                    
                    
                      
                      
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
                    
                     
                   
                     
                       
                          

        
        
        
        
            
        
        
        
        
        
                 

        

        
        
     
  

        
       

      
    

       
        

        
       


       
                            
                           
                           
                           
        
                
                    
        
        
        
