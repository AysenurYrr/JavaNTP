
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.restfb.*;
import com.restfb.types.FacebookType;
import org.bytedeco.opencv.global.opencv_imgproc;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;
import java.awt.image.RescaleOp;
import org.bytedeco.opencv.opencv_core.Mat;
import java.io.File;
import java.io.FileInputStream;

import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;

public class GriAydinlatParlat {
    private static boolean grilestir = false; // Grileştirme durumunu tutmak için bir bayrak değişkeni
    private static boolean aydinlat = false; // Aydınlatma durumunu tutmak için bir bayrak değişkeni
    private static boolean parlat = false;   // Parlaklık durumunu tutmak için bir bayrak değişkeni
    public static BufferedImage capturedImage = null;
    public static boolean mouseClick = false;
    static File resim_dosyasi;
    static BufferedImage foto;

    public static void main(String[] args) {

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        try {
            grabber.start();

            Java2DFrameConverter converter = new Java2DFrameConverter();
            final BufferedImage[] image = {converter.getBufferedImage(grabber.grab(), 1)};

            JFrame frame = new JFrame("Kamera Görüntüsü");
            // Arka plan rengi
            Color backgroundColor = new Color(234, 234, 234); // Yumuşak Gri

            // Buton renkleri
            Color Renk1 = Color.decode("#3A2D27");
            Color Renk2 = Color.decode("#A99165");
            Color Renk3 = Color.decode("#C9C69F");
            Color Renk4 = Color.decode("#594A3C");

            // Arka plan rengini ayarla
            frame.getContentPane().setBackground(backgroundColor);
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(image[0], 8, 3, null);
                }

            };
            panel.setBackground(Renk4);

            JButton grilestirButton = new JButton("Grileştir");
            JButton parlatButton = new JButton("Parlat");
            JButton aydinlatButton = new JButton("Aydınlat");

            // Arka plan rengini değiştir
            grilestirButton.setBackground(Renk2);
            parlatButton.setBackground(Renk2);
            aydinlatButton.setBackground(Renk2);


            grilestirButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    grilestir = !grilestir;
                    aydinlat = false;
                    parlat = false;

                }
            });

            aydinlatButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    aydinlat = !aydinlat;
                    grilestir = false;
                    parlat = false;
                }
            });

            parlatButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    parlat = !parlat;
                    grilestir = false;
                    aydinlat = false;
                }
            });
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(image[0].getWidth(), image[0].getHeight());
            frame.setSize(1385, 560);
            frame.setResizable(false); // Ekran boyutunu değiştirmeyi engelle
            frame.setLocationRelativeTo(null); // Ekranın ortasında konumlandır
            frame.setVisible(true);
            frame.setLayout(new BorderLayout());

            // Altta bulunan butonlar bölümü
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bottomPanel.setBackground(Renk1);

            JButton fotoCekButton = new JButton("Fotoğraf Çek");
            bottomPanel.add(fotoCekButton);
            JButton fotoKaydetButton = new JButton("Fotoğraf Kaydet");
            bottomPanel.add(fotoKaydetButton);
            JButton paylasButton = new JButton("Fotografi Twitter'a Gonder");
            bottomPanel.add(paylasButton);

            fotoCekButton.setBackground(Renk3);
            paylasButton.setBackground(Renk3);
            fotoKaydetButton.setBackground(Renk3);

            frame.add(bottomPanel, BorderLayout.SOUTH);

            fotoCekButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        if (capturedImage == null){
                            capturedImage = image[0];
                        }
                        JPanel rightPanel = new JPanel(new GridBagLayout());
                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.anchor = GridBagConstraints.NORTHEAST;
                        JLabel imageLabel = new JLabel(new ImageIcon(capturedImage));
                        gbc.gridx = 0;
                        gbc.gridy = 0;
                        gbc.gridwidth = 2; // 2 hücre genişliği
                        gbc.gridheight = 1; // 1 hücre yüksekliği
                        gbc.insets = new Insets(0, 0, 0, 0); // Üst boşluğu kaldırma
                        rightPanel.add(imageLabel, gbc);

                        frame.add(rightPanel, BorderLayout.EAST);
                        frame.revalidate();
                        foto = capturedImage;

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            fotoKaydetButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    File dosya=new File(Fotograf.dosyaSec().toString()+".jpg");
                    resim_dosyasi=dosya;
                    try {
                        ImageIO.write(foto, "jpg", dosya);
                        JOptionPane.showMessageDialog(null, "Kaydedildi", "kayit", JOptionPane.INFORMATION_MESSAGE);
                    }catch (Exception er){er.printStackTrace();}
                }
            });

            paylasButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    fotoPaylas();
                }
            });

            // Soldaki butonlar bölümü
            JPanel buttonsPanel = new JPanel(new GridLayout(3, 1));
            buttonsPanel.setBackground(Renk1);

            buttonsPanel.add(grilestirButton);
            buttonsPanel.add(aydinlatButton);
            buttonsPanel.add(parlatButton);

            frame.getContentPane().add(panel);
            frame.add(buttonsPanel,BorderLayout.WEST);

            while (frame.isVisible()) {
                image[0] = converter.getBufferedImage(grabber.grab(), 1);

                if (grilestir) {
                    BufferedImage grayImage = new BufferedImage(image[0].getWidth(), image[0].getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                    ColorConvertOp op = new ColorConvertOp(null);
                    op.filter(image[0], grayImage);
                    image[0] = grayImage;
                    capturedImage = grayImage;


                } else if (aydinlat) {
                    float brightness = 1.2f;    // Aydınlatma faktörü
                    float offset = 0;           // Aydınlatma ofseti
                    RescaleOp rescaleOp = new RescaleOp(brightness, offset, null);
                    rescaleOp.filter(image[0], image[0]);
                    capturedImage = rescaleOp.filter(image[0], image[0]);
                } else if (parlat) {

                    float brightness = 1.2f;    // Parlaklık faktörü
                    float offset = 20;          // Parlaklık ofseti
                    RescaleOp rescaleOp = new RescaleOp(brightness, offset, null);
                    rescaleOp.filter(image[0], image[0]);
                    capturedImage = rescaleOp.filter(image[0], image[0]);

                }
                panel.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                grabber.stop();
                grabber.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void fotoPaylas() {
        //30 temmuza kadar geçerli
        String accessToken = "EAAT7Kw6YKxkBAEfY9F4iC7icFFeX3ZA5ysohwpZAo13uHod14Puk8UOQwG9d0AkE6ZCo9gkO3U93ejK8WeKIsZBvKX7gX8qpXS7ScH7ZCZA3mke9sxWFFiQA7QewgdUuNsgNaV3dff6BZByp16J6Bwq0iGNePofQuiaEKblNmFTd7KcHJYkEa5REn2S3ombUXMZD";
        //
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);

        // Paylaşmak istediğiniz fotoğrafın yolu
        String photoPath = resim_dosyasi.toString(); // "C:\\Users\\Beytullah\\res3.jpg";

        try {
            FileInputStream fis = new FileInputStream(photoPath);
            if (fis != null) {
                // Fotoğrafı yükleyin
                FacebookType response = facebookClient.publish("/100093235084656/photos", FacebookType.class,
                        BinaryAttachment.with("resim",fis),
                        Parameter.with("message", "deneme"));

                // Yüklenen fotoğrafın ID'sini alın
                String photoId = response.getId();

                String yuklemeMesaj="Fotoğraf başarıyla yüklendi. ID: " + photoId.toString();
                JOptionPane.showMessageDialog(null, yuklemeMesaj, "Kayıt", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("Fotoğrafın veri içeriği eksik!");
            }
        }catch (Exception e){e.printStackTrace();}
    }
}
