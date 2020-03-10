package com.example.a20151094010099.deteccao;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import MQTT.MqTTOptions;

public class DeteccaoDePessoas extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    MqTTOptions mqtt = new MqTTOptions();

    Mat mRgba, gray;
    JavaCameraView javaCameraView;
    CascadeClassifier cascadeClassifier;
    int absoluteFaceSize;
    Mat rgbaA;
    int cont = 0;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    //initializeCascade();
                    javaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    TextView textView;
    String email, senha;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deteccao_de_pessoas);

        email = "italofreitas613@gmail.com";
        senha = "italo2.0";

        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        textView = (TextView) findViewById(R.id.msg);
        try {
            final int cont1 = 0, cont2 = 0, cont3 = 2;
            mqtt.conect(getApplicationContext()).setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    textView.setText(new String(message.getPayload()));

                    if (topic.equals("/casa/presenca")) {
                        if ((new String(message.getPayload())).equals("2")) {
                            initializeCascade();
                        }
                        if ((new String(message.getPayload())).equals("3")) {
                            cascadeClassifier = null;
                        }
                    }

                    if (topic.equals("/casa/chuva")) {
                        if (!(new String(message.getPayload())).equals(cont1 + "")) {
                            textView.setText((new String(message.getPayload())));
                            mandaEmail2("Chuva", "Esta chovendo na sua casa");
                        }
                    }

                    if (topic.equals("/casa/fogo")) {
                        if ((new String(message.getPayload())).equals(1)) {
                            if (!(new String(message.getPayload())).equals(cont2 + "")) {
                                textView.setText((new String(message.getPayload())));
                                mandaEmail2("Fogo", "Sua casa esta pegando fogo");
                            }
                        }
                        if ((new String(message.getPayload())).equals(3)) {

                            if (!(new String(message.getPayload())).equals(cont3 + "")) {
                                textView.setText((new String(message.getPayload())));
                                mandaEmail2("Gas", "Esta tendo um vazamento de gas em sa residencia");
                            }
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        }
    }

    private void initializeCascade() {


        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];

            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //javaCameraView.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        gray = new Mat(height, width, CvType.CV_8UC1);
        rgbaA = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Imgproc.cvtColor(mRgba, gray, Imgproc.COLOR_RGB2GRAY);

        MatOfRect faces = new MatOfRect();
        gray = rotate(gray, -90);
        mRgba = rotate(mRgba, -90);

        if (cascadeClassifier != null) {

            //Imgproc.threshold(gray,gray,0,255,Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
            cascadeClassifier.detectMultiScale(gray, faces, 1.1, 2, 2 | Objdetect.CASCADE_SCALE_IMAGE,
                    new Size(absoluteFaceSize, absoluteFaceSize), new Size());

        }

        Rect[] arrayFaces = faces.toArray();
        for (int i = 0; i < faces.toArray().length; i++) {
            Core.rectangle(mRgba, arrayFaces[i].tl(), arrayFaces[i].br(), new Scalar(0, 255, 0, 255), 3);
            rgbaA = mRgba;

            Bitmap imgL = Bitmap.createBitmap(rgbaA.cols(), rgbaA.rows(), Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(rgbaA, imgL);
            if (cont == 0) {
                mandaEmail(salvarImagem(imgL));
                cont++;
            }
        }
        return mRgba;
    }

    public String salvarImagem(Bitmap bitmap) {

        String nomeArquivo = "";
        Random random = new Random();
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] bytes = stream.toByteArray();
            nomeArquivo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/image-" + random.nextInt(9) + random.nextInt(9) +
                    random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + random.nextInt(9) + ".png";
            FileOutputStream fos = new FileOutputStream(nomeArquivo);
            fos.write(bytes);
            fos.close();
            Toast.makeText(DeteccaoDePessoas.this, "Imagem Salva", Toast.LENGTH_SHORT).show();
            // textView.setText("Imagem Salva");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nomeArquivo;
    }

    public void mandaEmail(final String filename) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    //StrictMode.setThreadPolicy(policy);

                    Properties properties = new Properties();
                    properties.put("mail.smtp.host", "smtp.googlemail.com");
                    properties.put("mail.smtp.socketFactory.port", "465");
                    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    properties.put("mail.smtp.auth", "true");
                    properties.put("mail.smtp.port", "465");

                    session = Session.getDefaultInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(email, senha);
                        }
                    });

                    if (session != null) {
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(email));
                        message.setSubject("Conhecido seu?");
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("italodias72@gmail.com"));
                        message.setContent("Entrou uma pessoa", "text/html; charset=utf-8");
                        MimeBodyPart anexo = new MimeBodyPart();
                        FileDataSource source = new FileDataSource(filename);
                        anexo.setDataHandler(new DataHandler(source));
                        anexo.setFileName(filename);
                        Multipart multi = new MimeMultipart();
                        multi.addBodyPart(anexo);
                        message.setContent(multi);
                        Transport.send(message);
                        // Toast.makeText(Tela1.this, "Mensagem Enviada", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void mandaEmail2(final String assunto, final String texto) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    //StrictMode.setThreadPolicy(policy);

                    Properties properties = new Properties();
                    properties.put("mail.smtp.host", "smtp.googlemail.com");
                    properties.put("mail.smtp.socketFactory.port", "465");
                    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    properties.put("mail.smtp.auth", "true");
                    properties.put("mail.smtp.port", "465");

                    session = Session.getDefaultInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(email, senha);
                        }
                    });

                    if (session != null) {
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(email));
                        message.setSubject(assunto);
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("italodias72@gmail.com"));
                        message.setContent(texto, "text/html; charset=utf-8");
                        Transport.send(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Mat rotate(Mat image, double angle) {
        //Calculate size of new matrix
        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));

        int newWidth = (int) (image.width() * cos + image.height() * sin);
        int newHeight = (int) (image.width() * sin + image.height() * cos);

        // rotating image
        Point center = new Point(newWidth / 2, newHeight / 2);
        Mat rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0); //1.0 means 100 % scale

        Size size = new Size(newWidth, newHeight);
        Imgproc.warpAffine(image, image, rotMatrix, image.size());

        return image;
    }
}
