import java.io.*;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
class GenelUye {

    public String getUyeTuru() {
        return "#GENEL UYELER";
    }

    public void uyeEkle() {
        String isim, soyisim, mail;

        Scanner giris = new Scanner(System.in);
        System.out.println("İsim girin:");
        isim = giris.next();
        System.out.println("Soyisim girin:");
        soyisim = giris.next();
        System.out.println("Mail girin:");
        mail = giris.next();

        //eklenecek olan satır bu şekilde olacak
        String paragraf = isim + "\t" + soyisim + "\t" + mail + "\n";
        System.out.println(paragraf);

        //Paragraf yazma fonksiyonunu cagiriyoruz.
        dosyaOkuYaz(paragraf, getUyeTuru());
    }

    public static void dosyaOkuYaz(String bilgi, String uyeTuru) {

        try {
            //Gerekli tanimlamalari burada yapiyoruz
            FileReader fileReader = new FileReader("dosya.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            BufferedWriter writer = null;

            String satir;
            StringBuilder yeni_satirlar = new StringBuilder();

            //Satir okurken null dönene kadar okuma yapacağız.
            while ((satir = bufferedReader.readLine()) != null) {

                //Üye türüne göre yeni satırın nereye yazdırılacağına karar vermemizi sağlıyoruz (Elit Üye - Genek Üye)
                //Burada tüm metin ve satırlar okunur. Tamamen yeni bir metin oluşturulur. Yeni oluşturulan metinde yeni üye bulunur.
                if (satir.startsWith(uyeTuru)) {
                    yeni_satirlar.append(uyeTuru).append("\n").append(bilgi);
                } else {
                    yeni_satirlar.append(satir).append("\n");
                }
            }
            System.out.println(yeni_satirlar);
            bufferedReader.close();

            //Dosya yazma işlemini gerçekleştirmemizi sağlıyor. Yeni oluşturdugumuz metini dosya.txt'ye kaydediyoruz
            writer = new BufferedWriter(new FileWriter("dosya.txt"));
            writer.write(yeni_satirlar.toString());
            writer.close();


        } catch (IOException e) {
            System.out.println("Dosyaya yazma işlemi başarısız oldu: " + e.getMessage());
        }
    }

    //Toplu mesaj işlemleri bulunuyor. Bu bizim ikinci menümüzdeki işlemleri yapmamızı sağlar.
    public static void TopluMesajGonder(int y){
        try {
            //Temel tanımlamalar
            FileReader fileReader = new FileReader("dosya.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            boolean control = false;
            String satir;
            String mail_adress;
            String recepient, myAccountEmail, password,Subject, Text;

            Scanner giris = new Scanner(System.in);

            //Mail ve parola bilgileri, konu metin gibi şeyleri kullanıcıdan alıyoruz.
            System.out.println("Kendi mailinizi girin:");
            myAccountEmail = giris.next();
            System.out.println("Şifre girin:");
            password = giris.next();
            System.out.println("Konu girin:");
            Subject = giris.next();
            System.out.println("Metin girin:");
            Text = giris.next();

            // Kullanıcı seçimine göre kimlere mesaj girileceğine karar veriliyor.
            if (y  == 1){ //Elit üyelere mail
                while ((satir = bufferedReader.readLine()) != null ){
                    if (satir.contains("#ELIT UYELER")){
                        continue;
                    }
                    else if (satir.contains("@")){ //Satır içerisinde "@" bulunuyorsa işlem yapılacak.
                        String[] altDizeler=satir.split("\\s+"); //split ile ayırarak 3. kelimeyi alıcı olarak belirleyeceğiz.
                        recepient=altDizeler[2];

                        //Burada adrese mail gonderiyoruz. Mail gönderme fonksiyonu çalışıyor.
                        GenelUye.SendMail(recepient, myAccountEmail,password,Subject,Text); //Buraya kendi mail ve şifrenizi yazınız.
                        System.out.println("Mesajiniz adrese gönderiliyor" + recepient);
                    }
                    else if (satir.contains("#GENEL UYELER")){
                        break;
                    }
                }
            }
            else if (y == 2){//Genel Üyelere mail, Yukarıda olan işlemlerin benzeri.
                while ((satir = bufferedReader.readLine()) != null ){
                    if (satir.contains("#GENEL UYELER")){ //Genel üyeler gelene kadar okuma yapmamızı sağlıyor.
                        control = true;
                    }
                    else if (control && satir.contains("@")){ //Elit üyelerden sonra genel üyeler geldiği için bu şekilde yazıldı
                        String[] altDizeler=satir.split("\\s+");
                        recepient=altDizeler[2];

                        //Mail adresine mail gönder.
                        GenelUye.SendMail(recepient, myAccountEmail,password,Subject,Text); //Buraya kendi mail ve şifrenizi yazınız.
                    }
                }
            }
            else if (y == 3){
                while ((satir = bufferedReader.readLine()) != null ){
                    if(satir.contains("@")){ //Tüm "@" işaretli satırlarda 3. eleman mail olarak kabul edilecek ve mail gönderilecek
                        String[] altDizeler=satir.split("\\s+");
                        recepient=altDizeler[2];

                        //Mail adresine mail gönder.
                        GenelUye.SendMail(recepient, myAccountEmail,password,Subject,Text); //Buraya kendi mail ve şifrenizi yazınız.

                    }
                }
            }


        } catch (IOException e) {
            System.out.println("Dosyaya yazma işlemi başarısız oldu: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static void SendMail(String recepient, String myAccountEmail, String password, String subject, String text) throws Exception {
        //Temel hazırlıklar belirleniyor ve yapılıyor.
        System.out.println("\nMail göndermeye hazırlanılıyor.");
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        System.out.println("Mailiniz gönderiliyor");

        //Burada eşleşip eşleşmeme durumu kontrol edilmekte. Hata alınırsa mail ayarlarınızda bir problem olabilir.
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });
        //Mesaj oluşturma fonksiyonudur. Verilen bilgilerle uygun mail oluşturulacakttır.
        Message message = prepareMessage(session, myAccountEmail, recepient, subject,text);

        //Oluşturulan maili gönderir
        Transport.send(message);
        System.out.println("Mail gönderimi başarılı.");
    }

    private static Message prepareMessage(Session session, String myAccountEmail, String recepient, String subject, String text){
        //Gönderilecek olan Mailin konusu başlığı text'i burada belirlenmekte
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail)); //internet adresi
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient)); //alıcı
            message.setSubject(subject); //konu
            message.setText(text);//text
            return message; //Fonksiyonun sonucunda mail döndürülür
        } catch (Exception ex){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}

class ElitUye extends GenelUye{
    //Genel Üyelerden kalıtımla türetilmiştir. Bu class üye türünü "#ELIT UYELER" Stringi olarak döndürür.
    //Bu class sayesinde Elit Üyelerin işlemleri için aynı fonksiyonlar rahatlıkla kullanılabilir.
    @Override
    public String getUyeTuru() {
        return "#ELIT UYELER";
    }

}


public class Main {

    public static void main(String[] args) throws Exception {
        ElitUye elitUye = new ElitUye();
        GenelUye genelUye = new GenelUye();
        String isim, soyisim, mail;
        while (true) {
            //İlk menü için işlemler
            System.out.println("1- Elit Üye Ekleme\t");
            System.out.println("2- Genel Üye Ekleme\t");
            System.out.println("3- Mail Gönderme\n\t");

            Scanner giris = new Scanner(System.in);

            System.out.println("Seciminizi girin:");
            int x = giris.nextInt();
            if (x==1){
                //Kullanıcı 1 bastığında isim soy isim ve email alıp ekteki dosyaya bilgileri kayıt edecek. Dosya formatı
                //aşağıdaki gibi. Bilgiler arasında tab karakteri var.
                elitUye.uyeEkle();

            }
            else if(x==2){
                //Kullanıcı 1 bastığında isim soy isim ve email alıp ekteki dosyaya bilgileri kayıt edecek. Dosya formatı
                //aşağıdaki gibi. Bilgiler arasında tab karakteri var.
                genelUye.uyeEkle();
            }

            else if(x==3){
                //Kullanıcı 3’ bastığında tekrar bir menü çıkacak. İkinci menü işlemleri
                System.out.println("1- Elit Üyelere Mail\t");
                System.out.println("2- Genel Üyelere Mail\t");
                System.out.println("3- Tüm Üyelere Mail\n\t");
                System.out.println("Seciminizi girin:");
                int y = giris.nextInt();

                if (y == 1){
                    GenelUye.TopluMesajGonder(y);
                }
                else if (y == 2){
                    ElitUye.TopluMesajGonder(y);
                }
                else if (y == 3){
                    GenelUye.TopluMesajGonder(y);

                }
            }
        }
    }
}
