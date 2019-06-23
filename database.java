DATABASE PACKAGES:

package pack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;

public class Db {
    
    public static Connection getConnection(){
        Connection con =null;
try{
    String url="jdbc:mysql://localhost:3306/mech";
    String driver="com.mysql.jdbc.Driver";
    Class.forName(driver).newInstance();
    con = DriverManager.getConnection(url, "root", "");
    System.out.println("Database Connected");
    
}   
catch(Exception e)
    
{
    
}
        return con;
}
    public static void main(String[] args)
    {
        
    }
}


NEW SERVLET PACKAGE:

package pack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class NewServlet extends HttpServlet {

    private static java.sql.Date getCurrentDate() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Date(today.getTime());
    }

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection con;
        PreparedStatement pstm = null;
        String subject = "";
        String cd = "";
        String a = (String) request.getSession().getAttribute("c");
        System.out.println("User Name"+a);
        try {
            boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
            if (!isMultipartContent) {
                return;
            }
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                List<FileItem> fields = upload.parseRequest(request);
                Iterator<FileItem> it = fields.iterator();
                if (!it.hasNext()) {

                    return;
                }
                while (it.hasNext()) {
                    FileItem fileItem = it.next();
                    if (fileItem.getFieldName().equals("subject")) {
                        subject = fileItem.getString();
                        System.out.println("Subject" + subject);
                    } else {
                    }
                    boolean isFormField = fileItem.isFormField();
                    if (isFormField) {
                    } else {
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mech", "root", "");
                            pstm = con.prepareStatement("insert into files (file,subject,filetype,filename,CDate,owner,size,data)values(?,?,?,?,?,?,?,?)");
                            System.out.println("getD " + fileItem.getName());
                            String str = getStringFromInputStream(fileItem.getInputStream());
                            String cipher = new TrippleDes().encrypt(str);
                            System.out.println(str);
                            //for get extension from given file
                            String b = fileItem.getName().substring(fileItem.getName().lastIndexOf('.'));
                            System.out.println("File Extension"+b);
                            pstm.setBinaryStream(1, fileItem.getInputStream());
                            pstm.setString(2, subject);
                            pstm.setString(3, b);
                            pstm.setString(4, fileItem.getName());
                            pstm.setDate(5, getCurrentDate());
                            pstm.setString(6, a);
                            pstm.setLong(7, fileItem.getSize());
                            pstm.setString(8, cipher);
                            int i = pstm.executeUpdate();
                            if (i == 1) {
                                response.sendRedirect("fileupload.jsp?msg= sucess..!");
                            } else {
                                response.sendRedirect("fileupload.jsp?msgg= NOT sucess..!");
                            }
                            con.close();

                        } catch (Exception e) {
                            out.println(e.toString());
                        }
                    }

                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                Logger.getLogger(NewServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            out.close();
        }
    }
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

SIMPLE FTP CLIENT PACKAGE:


package pack;




import java.net.*;
import java.io.*;

public class SimpleFTPClient {

    /** The URL connection object */
    private URLConnection m_client;
    /** The FTP host/server to be connected */
    private String host;
    /** The FTP user */
    private String user;
    /** The FTP user’s password */
    private String password;
    /** The remote file that needs to be uploaded or downloaded */
    private String remoteFile;
    /** The previous error message triggered after a method is called */
    private String erMesg;
    /** The previous success message after any method is called */
    private String succMesg;

    public SimpleFTPClient() {
    }

    /** Setter method for the FTP host/server */
    public void setHost(String host) {
        this.host = host;
    }

    /** Setter method for the FTP user */
    public void setUser(String user) {
        this.user = user;
    }

    /** Setter method for the FTP user’s password */
    public void setPassword(String p) {
        this.password = p;
    }

    /** Setter method for the remote file, this must include the sub-directory path relative
    to the user’s home directory, e.g you’e going to download a file that is within a sub directory
    called "sdir", and the file is named "d.txt", so you shall include the path as "sdir/d.txt"
     */
    public void setRemoteFile(String d) {
        this.remoteFile = d;
    }

    /** The method that returns the last message of success of any method call */
    public synchronized String getLastSuccessMessage() {
        if (succMesg == null) {
            return "";
        }
        return succMesg;
    }

    /** The method that returns the last message of error resulted from any exception of any method call */
    public synchronized String getLastErrorMessage() {
        if (erMesg == null) {
            return "";
        }
        return erMesg;
    }

    /** The method that handles file uploading, this method takes the absolute file path
    of a local file to be uploaded to the remote FTP server, and the remote file will then
    be transfered to the FTP server and saved as the relative path name specified in method setRemoteFile
    @param localfilename – the local absolute file name of the file in local hard drive that needs to
    FTP over
     */
   public synchronized boolean uploadFile(InputStream is) {
    //public synchronized boolean uploadFile(String localfilename) {
        try {

         //   InputStream is = new FileInputStream(localfilename);
            BufferedInputStream bis = new BufferedInputStream(is);
            OutputStream os = m_client.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            byte[] buffer = new byte[1024];
            int readCount;

            while ((readCount = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, readCount);
            }
            bos.close();

            this.succMesg = "Uploaded!";

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            StringWriter sw0 = new StringWriter();
            PrintWriter p0 = new PrintWriter(sw0, true);
            ex.printStackTrace(p0);
            erMesg = sw0.getBuffer().toString();

            return false;
        }
    }

    /** The method to download a file and save it onto the local drive of the client in the specified absolut path
    @param localfilename – the local absolute file name that the file needs to be saved as */
    public synchronized boolean downloadFile(String localfilename) {
        try {
            InputStream is = m_client.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            System.out.println(">>>>>>>>>>>"+localfilename);
            OutputStream os = new FileOutputStream(localfilename);
            BufferedOutputStream bos = new BufferedOutputStream(os);

            byte[] buffer = new byte[1024];
            int readCount;

            while ((readCount = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, readCount);
            }
            bos.close();
            is.close(); // close the FTP inputstream
            this.succMesg = "Downloaded!";

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            StringWriter sw0 = new StringWriter();
            PrintWriter p0 = new PrintWriter(sw0, true);
            ex.printStackTrace(p0);
            erMesg = sw0.getBuffer().toString();

            return false;
        }
    }

    /** The method that connects to the remote FTP server */
    public synchronized boolean connect() {
        try {
            URL url = new URL("ftp://" + user + ":" + password + "@" + host + "/" + remoteFile + ";type=i");
            m_client = url.openConnection();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+"ftp://" + user + ":" + password + "@" + host + "/" + remoteFile + ";type=i");
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            StringWriter sw0 = new StringWriter();
            PrintWriter p0 = new PrintWriter(sw0, true);
            ex.printStackTrace(p0);
            erMesg = sw0.getBuffer().toString();
            return false;
        }
    }
    public static void main(String arg[]) {
        SimpleFTPClient f = new SimpleFTPClient();
        f.setHost("ftp.drivehq.com");
        f.setUser("");
        f.setPassword("");
        f.setRemoteFile("c.txt");
        boolean connected = f.connect();

    }
}


TRIPPLE DES PACKAGE:


package pack;

import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.apache.commons.codec.binary.Base64;


public class TrippleDes {
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME =
            "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;

    public TrippleDes() throws Exception {
        myEncryptionKey = "ThisIsSpartaThisIsSparta";
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        ks = new DESedeKeySpec(arrayBytes);
        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
    }

    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText =
                    unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }
}


MAIL SENDING PACKAGE:

package pack;


import com.sun.mail.smtp.SMTPTransport;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class mail_Senddd {

    public static boolean sendMail(String msg, String userid, String to) {
        Properties props = new Properties();
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        // Assuming you are sending email from localhost
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("hcl.mailme@gmail.com", "sundar@123");
            }
        });

        System.out.println("Message   " + msg);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userid));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject("Secret key ");
            message.setText(msg);

            Transport.send(message);

            System.out.println("Done");
            return true;

        } catch (MessagingException e) {
            System.out.println(e);
            e.printStackTrace();
            return false;
            // throw new RuntimeException(e);
        }
    }

    public static void main(String arg[]) {
        // sendMail("sssssssssss");
    }
}






