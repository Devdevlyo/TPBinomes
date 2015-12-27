/**
 * Created by nathan on 09/12/15.
 */
package reseau;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.HPos;
import javafx.application.*;
import javafx.scene.shape.Circle;

import reseau.client.Client;



public class MainClientFX extends Application{

    Client clientFX;
    ListView<String> tField;

    public static void main(String[] args)
    {
        Application.launch(MainClientFX.class,args);
    }
    
    public void ajouterElement(String element)
    {
      Platform.runLater(() ->tField.getItems().add(element));
    }

    @Override
    public void start(Stage primaryStage)
    {
        clientFX = new Client (this);
        primaryStage.setTitle("IRC with Java RMI & JavaFX");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(0, 0, 0, 0));

        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);

        Label userName = new Label("Message:");
        grid.add(userName, 0, 3);
        
        Label pseudo = new Label ("Pseudo :");
        grid.add(pseudo, 0, 0);
        

        //Zone ou on peut taper du texte
        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 3);
        
        final TextField connectionTextField = new TextField();
        grid.add(connectionTextField, 1, 0);
        
        //implementation du bouton
        Button send = new Button("Envoyer");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbBtn.getChildren().add(send);
        grid.add(hbBtn, 2, 3);
        Button connect = new Button ("Connexion/Déconnexion");
        HBox hbBtncon = new HBox(10);
        hbBtncon.setAlignment(Pos.BOTTOM_LEFT);
        hbBtncon.getChildren().add(connect);
        grid.add(hbBtncon, 2, 0);
        
        Circle circle = new Circle();
        circle.setCenterX(100.0f);
        circle.setCenterY(100.0f);
        circle.setRadius(5.0f);
        circle.setFill(Color.WHITE);
        grid.add(circle,4,0);
        
        //Message quand bouton appuye
        send.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                java.lang.CharSequence message;
                message = userTextField.getCharacters();
                String finalmessage = new StringBuilder(message).toString();
                if(!finalmessage.equals("") && clientFX.getConnecte())
                {
                    clientFX.envoyerTexte(finalmessage);
                    userTextField.clear();
                }
            }
        });    
        
         //Message quand bouton appuye pour se connecter
        connect.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                java.lang.CharSequence message;
                message = connectionTextField.getCharacters();
                String finalmessage = new StringBuilder(message).toString();
                if (!finalmessage.equals("") && !clientFX.getConnecte())
                {
                    clientFX.identifiant(finalmessage);
                    clientFX.connexion();
                    if (clientFX.getConnecte())
                    {
                    connectionTextField.setEditable(false);
                    circle.setFill(Color.GREEN);
                    }
                    else {
                    circle.setFill(Color.RED);
                    }
                }
                else if (clientFX.getConnecte()){
                    clientFX.deconnexion();
                    connectionTextField.setEditable(true);
                    circle.setFill(Color.WHITE);
                }
            }
            
        });

        // Zone ou afficher le chat
        tField = new ListView<String>();
        tField.setPrefWidth(150);
        tField.setPrefSize(350, 500);
        tField.autosize();
        GridPane.setHalignment(tField, HPos.CENTER);
        grid.add(tField, 1, 2);

        primaryStage.show();

    }

}
