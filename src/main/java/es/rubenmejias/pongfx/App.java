package es.rubenmejias.pongfx;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class App extends Application {
    int score;
    int highScore;
    
    int ballCenterX = 10;
    int ballCurrentSpeedX = 3;
    int ballCenterY = 30;
    int ballCurrentSpeedY = 3;
    final int SCENE_TAM_X = 600;
    final int SCENE_TAM_Y = 400;
    final int STICK_WIDTH = 7;
    final int STICK_HEIGHT = 50;
    int stickPosY = (SCENE_TAM_Y - STICK_HEIGHT) / 2;
    int stickCurrentSpeed = 0;
    final int TEXT_SIZE = 24;
    
    Text textScore;
    Pane root = new Pane();
    
    @Override
      public void start(Stage stage) {
          
          Scene scene = new Scene(root, SCENE_TAM_X, SCENE_TAM_Y, Color.BLACK);
          stage.setTitle("PongFX");
          stage.setScene(scene);
          stage.show();
          
          textScore = new Text("0");
          
          Circle circleBall = new Circle(ballCenterX, ballCenterY, 7, Color.WHITE);
          root.getChildren().add(circleBall);
          Rectangle rectStick = new Rectangle(SCENE_TAM_X*0.9, stickPosY, STICK_WIDTH, STICK_HEIGHT);
          rectStick.setFill(Color.WHITE);
          root.getChildren().add(rectStick);
          
          //LAYOUTS PARA MOSTRAR PUNTUACIONES

          //Layout principal
          HBox paneScores = new HBox();
          paneScores. setTranslateY(20) ;
          paneScores. setMinWidth(SCENE_TAM_X) ;
          paneScores. setAlignment (Pos.CENTER) ;
          paneScores. setSpacing(100) ;
          root.getChildren().add(paneScores) ;
          

          //Layout para puntuacién actual
          HBox paneCurrentScore = new HBox();
          paneCurrentScore. setSpacing(10) ;
          paneScores.getChildren().add(paneCurrentScore) ;

          //Layout para puntuacién mixima
          HBox paneHighScore = new HBox();
          paneHighScore. setSpacing(10) ;
          paneScores.getChildren().add(paneHighScore) ;

          //Texto de etiqueta para la puntuacién
          Text textTitleScore = new Text("Score:");
          textTitleScore.setFont(Font.font(TEXT_SIZE));
          textTitleScore.setFill(Color.WHITE);

          //Texto para la puntuacin
          Text textScore = new Text("0");
          textScore. setFont (Font. font (TEXT_SIZE));
          textScore. setFill(Color.WHITE) ;

          //Texto de etiqueta para la puntuacién maxima
          Text textTitleHighScore = new Text("Max.Score:");
          textTitleHighScore. setFont (Font. font (TEXT_SIZE));
          textTitleHighScore.setFill(Color.WHITE) ;

          //Texto para la puntuacién mixima
          Text textHighScore = new Text("0");
          textHighScore. setFont (Font. font (TEXT_SIZE)) ;
          textHighScore. setFill(Color.WHITE) ;
          paneCurrentScore.getChildren().add(textTitleScore) ;
          paneCurrentScore.getChildren().add(textScore) ;
          paneHighScore.getChildren().add(textTitleHighScore);
          paneHighScore.getChildren().add(textHighScore);

          drawNet(10, 4, 30);
          
          
          resetGame();
          Timeline animationBall = new Timeline(
                  new KeyFrame(Duration.seconds(0.017), (ActionEvent ae) -> {
                      circleBall.setCenterX(ballCenterX);
                      ballCenterX += ballCurrentSpeedX;
                      if(ballCenterX >= SCENE_TAM_X) {
                          if (score > highScore) {
                              // CAMBIAR NUEVA PUNTACION MAS ALTA
                              highScore = score;
                              textHighScore.setText(String.valueOf(highScore));
                          }
                          // REINICIAR PARTIDA
                          resetGame();
                      }
                      if(ballCenterX <= 0) {
                          ballCurrentSpeedX = 3;
                      }
                      circleBall.setCenterY(ballCenterY);
                      ballCenterY += ballCurrentSpeedY;
                      if(ballCenterY >= SCENE_TAM_Y) {
                          ballCurrentSpeedY = -3;
                      }
                      if(ballCenterY <= 0) {
                          ballCurrentSpeedY = 3;
                      }
                      // ACTUALIZAR POSICION DE LA PALA
                      stickPosY += stickCurrentSpeed;
                      if(stickPosY < 0) {
                          // NO SOBREPASAR EL BORDE SUPERIOR DE LA VENTANA
                          stickPosY = 0;
                      } else {
                          // NO SOBREPASAR EL BORDE INFERIOR DE LA VENTANA
                          if(stickPosY > SCENE_TAM_Y - STICK_HEIGHT) {
                              stickPosY = SCENE_TAM_Y - STICK_HEIGHT;
                          }
                      }
                      // MOVER EL RECTANGULO DE LA PALA A LA POSICON ACTUAL
                      rectStick.setY(stickPosY);
                      Shape shapeColision = Shape.intersect(circleBall, rectStick);
                      boolean colisionVacia = shapeColision.getBoundsInLocal().isEmpty();
                      if(colisionVacia == false && ballCurrentSpeedX > 0) {
                          // COLISION DETECTADA. MOVEMOS LA BOLA HACIA LA IZQUIERDA
                          ballCurrentSpeedX = -3;
                          // INCREMENTAMOS LA PUNTUACION ACTUAL
                          score++;
                          textScore.setText(String.valueOf(score));
                          int collisionZone = getStickCollisionZone(circleBall, rectStick);
                          calculateBallSpeed(collisionZone);
                      }
                  })
          );
          
          scene.setOnKeyPressed((KeyEvent event) -> {
              switch(event.getCode()) {
                  case UP:
                      //PULSADA TECLA ARRIBA
                      stickCurrentSpeed = -6;
                      break;
                  case DOWN:
                      //PULSADA TECLA ABAJO
                      stickCurrentSpeed = 6;
                      break;
              }
          });
          scene.setOnKeyReleased((KeyEvent event) -> {
              stickCurrentSpeed = 0;
          });
                  
          animationBall.setCycleCount(Timeline.INDEFINITE);
          animationBall.play();
          
      }      
      
      public static void main(String[] args) {
          launch();
      }
      
      private void resetGame() {
          score = 0;
          textScore.setText(String.valueOf(score));
          ballCenterX = 10;
          ballCurrentSpeedY = 3;
          Random random = new Random();
          ballCenterY = random.nextInt(SCENE_TAM_Y);
      }
      
      private void drawNet(int portionHeight, int portionWidth, int portionSpacing) {
          // DIBUJO DE LA RED
          for(int i=0; i<SCENE_TAM_Y; i+=portionSpacing) {
              Line line = new Line(SCENE_TAM_X/2, i, SCENE_TAM_X/2, i+portionHeight);
              line.setStroke(Color.WHITE);
              line.setStrokeWidth(portionWidth);
              root.getChildren().add(line);
          }
      }
      
      private int getStickCollisionZone(Circle ball, Rectangle stick) {
          if (Shape.intersect(ball, stick).getBoundsInLocal().isEmpty()) {
            return 0;
          } else {
                double offsetBallStick = ball.getCenterY()- stick.getY();
                if(offsetBallStick < stick.getHeight() * 0.1) {
                  return 1;
                } else if (offsetBallStick < stick.getHeight() / 2) {
                  return 2;
                } else if (offsetBallStick >= stick.getHeight() / 2 && offsetBallStick < stick.getHeight() * 0.9) {
                  return 3;
                } else {
                  return 4;
                }
            }
      }
      
      
      
      private void calculateBallSpeed(int collisionZone) {
          switch(collisionZone) {
              case 0:
                  // NO HAY COLISION
                  break;
              case 1:
                  // HA COLISION EN LA ESQUINA SUPERIOR
                  ballCurrentSpeedX = -3;
                  ballCurrentSpeedY = -6;
                  break;
              case 2:
                  // HA COLISION EN LA ESQUINA SUPERIOR
                  ballCurrentSpeedX = -3;
                  ballCurrentSpeedY = -3;
                  break;
              case 3:
                  // HA COLISION EN EL LADO INFERIOR
                  ballCurrentSpeedX = -3;
                  ballCurrentSpeedY = 3;
                  break;
              case 4:
                  // HA COLISION EN LA ESQUINA INFERIOR
                  ballCurrentSpeedX = -3;
                  ballCurrentSpeedY = 6;
                  break;
          }
      }
      
}