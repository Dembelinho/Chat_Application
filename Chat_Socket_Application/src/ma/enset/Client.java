package ma.enset;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root2=new AnchorPane();
        VBox centring=new VBox();
        TextField name=new TextField();
        Button accept=new Button("Connect");
        centring.setSpacing(16); //

        //headText
        Text Txt1=new Text(" Chat Application ");
        Text Txt2 = new Text(" Chatroom ...");

        Txt1.setFont(Font.font("Arial", FontWeight.BOLD, 40));  //modifier
        Txt1.setLayoutX(135);
        Txt1.setLayoutY(70);
        Txt1.setFill(Color.valueOf("#110736"));
        root2.getChildren().add(Txt1);

        Txt2.setFont(Font.font("Arial",FontWeight.BOLD,20));
        Txt2.setLayoutX(260);
        Txt2.setLayoutY(100);
        Txt2.setFill(Color.valueOf("#110736"));
        root2.getChildren().add(Txt2);

        name.setPromptText("Username");

        centring.getChildren().add(name);
        centring.getChildren().add(accept);
        centring.setAlignment(Pos.CENTER);
        centring.setLayoutX(220);
        centring.setLayoutY(120);

        Text alert=new Text();
        alert.setFill(Color.DARKGRAY);
        centring.getChildren().add(0,alert);

        root2.getChildren().add(centring);

        Scene userScene=new Scene(root2);
        userScene.getStylesheets().add(getClass().getResource("style/style1.css").toExternalForm());
        stage.setScene(userScene);
//
        AnchorPane root=new AnchorPane();

        TextField message=new TextField();
        message.setStyle("-fx-border-color: #365088");
        message.setLayoutX(10);
        message.setLayoutY(320);
        message.setPrefWidth(520);
        message.setPrefHeight(30);

        Button send=new Button();
        send.setLayoutX(528);
        send.setLayoutY(317);
        ImageView imageView=new ImageView(getClass().getResource("style/send.png").toExternalForm());
        imageView.setFitWidth(30);
        imageView.setFitHeight(25);
        send.setGraphic(imageView);
        send.setBackground(null);

        ScrollPane msgPane=new ScrollPane();
        msgPane.setStyle("-fx-border-color: #365088");
        msgPane.setPrefWidth(560);
        msgPane.setPrefHeight(270);
        msgPane.setLayoutX(10);
        msgPane.setLayoutY(35);
        msgPane.setPrefViewportWidth(560);
        msgPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        msgPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Text to=new Text("To :");
        to.setX(8);
        to.setY(16);
        to.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        TextField receiver=new TextField();
        receiver.setStyle("-fx-border-color: #365088");
        receiver.setPrefWidth(534);
        receiver.setLayoutX(35);
        receiver.setLayoutY(0);

        AnchorPane contentPane=new AnchorPane();
        contentPane.setId("contentPane");
        contentPane.setStyle("-fx-background-color: WHITE");
        contentPane.setLayoutX(0);
        contentPane.setLayoutY(0);
        contentPane.setPrefWidth(510);
        contentPane.setPrefHeight(280);
        msgPane.setContent(contentPane);


        root.getChildren().add(message);
        root.getChildren().add(send);
        root.getChildren().add(to);
        root.getChildren().add(receiver);
        root.getChildren().add(msgPane);

        try {
            Socket socket =new Socket("localhost",1234);
            //input
            InputStream is = socket.getInputStream();
            InputStreamReader isr= new InputStreamReader(is);
            BufferedReader br= new BufferedReader(isr);
            //output
            OutputStream os =socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            //start the thread
            new Thread(() -> {
                try {
                    String server_Msg;
                    while ((server_Msg= br.readLine())!= null){

                        System.out.println(server_Msg);
                        double y=10;
                        if(contentPane.getChildren().size()-1>=0) {
                            y = contentPane.getChildren().get(contentPane.getChildren().size() - 1).getLayoutY()+20;
                        }
                        Button test=new Button(server_Msg);
                        test.setId("inMsg");
                        StackPane spane=new StackPane();
                        spane.getChildren().add(test);
                        spane.setPrefWidth(550);
                        spane.setPrefHeight(30);
                        spane.setLayoutX(0);
                        spane.setLayoutY(y+10);
                        spane.setAlignment(Pos.BASELINE_RIGHT);

                        Platform.runLater(()->{
                            contentPane.getChildren().add(spane);
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }).start();
            send.setOnAction(actionEvent -> {
                if (!message.getText().isEmpty()) {
                    double y = 10;
                    if (contentPane.getChildren().size() - 1 >= 0) {
                        y = contentPane.getChildren().get(contentPane.getChildren().size() - 1).getLayoutY() + 20;
                    }
                    Button test=new Button("ME : "+message.getText());
                    test.setLayoutX(5);
                    test.setLayoutY(y+10);
                    test.setId("outMsg");
                    contentPane.getChildren().add(test);
                    if(!receiver.getText().isEmpty()){
                        pw.println(receiver.getText()+"=>"+message.getText());
                    }
                    else{
                        pw.println(message.getText());
                    }

                    message.setText("");
                }
            });

            accept.setOnAction(actionEvent -> {
                if(!name.getText().isEmpty() ){
                    pw.println("name:"+name.getText());
                    Scene scene=new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("style/chat.css").toExternalForm());
                    stage.setScene(scene);
                }
                else {
                    alert.setText("Please Enter Your Username !!");
                }

            });

            stage.setHeight(400);
            stage.setWidth(600);
            stage.setTitle("MY Messenger");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
