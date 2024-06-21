import javafx.animation.AnimationTimer;
import javafx.application.*;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import java.util.*;


public class Main extends Application {
    private List<List<Cell>> matrix = new ArrayList<>();
    private class Cell{
        private boolean isLive;
        private Rectangle cell;
        public Cell(Rectangle cell){
            this.cell = cell;
        }

        public void setColor(Color color){
            cell.setFill(color);
        }


        public boolean getIsLive(){
            return isLive;
        }

        public void setIsLive(boolean isLive){
            this.isLive = isLive;
        }

    }
    private void startGame(Scene scene, GridPane grid, int row, int column){


        for (int i = 0; i < row; i++) {
            List<Cell> tmp = new ArrayList<>();
            for(int j = 0; j < column; j++){
                Rectangle shape = new Rectangle(j * 10 + 5, i * 10 + 5, 10, 10);
                shape.setId(i + " " + j);
                shape.setOnMouseClicked((event) -> {
                    String coordinate = shape.getId();
                    Scanner scan = new Scanner(coordinate);
                    int r = scan.nextInt();
                    int c = scan.nextInt();

                    if(shape.getFill() == Color.YELLOW){
                        shape.setFill(Color.BLACK);
                        matrix.get(r).get(c).setIsLive(false);
                    }else{
                        shape.setFill(Color.YELLOW);
                        matrix.get(r).get(c).setIsLive(true);
                    }


                });
                shape.setFill(Color.BLACK);
                shape.setStroke(Color.WHITE);
                shape.setVisible(true);
                grid.add(shape,i,j);

                Cell cell = new Cell(shape);
                tmp.add(cell);
            }
            matrix.add(tmp);
        }


    }


    private int helperSimulation(int row, int column) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = column - 1; j <= column + 1; j++) {
                if (i >= 0 && i < matrix.size() && j >= 0 && j < matrix.get(0).size() && matrix.get(i).get(j).getIsLive()) {
                    count++;
                }
            }
        }
        if (matrix.get(row).get(column).getIsLive()) {
            count--;
        }
        return count;
    }

    private void simulation() {
        List<List<Boolean>> newStates = new ArrayList<>();
        for (int i = 0; i < matrix.size(); i++) {
            List<Boolean> newRow = new ArrayList<>();
            for (int j = 0; j < matrix.get(0).size(); j++) {
                int countLiveCells = helperSimulation(i, j);
                if (countLiveCells == 3 || (countLiveCells == 2 && matrix.get(i).get(j).getIsLive())) {
                    newRow.add(true);
                } else {
                    newRow.add(false);
                }
            }
            newStates.add(newRow);
        }

        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(0).size(); j++) {
                matrix.get(i).get(j).setIsLive(newStates.get(i).get(j));
                if (newStates.get(i).get(j)) {
                    matrix.get(i).get(j).setColor(Color.YELLOW);
                } else {
                    matrix.get(i).get(j).setColor(Color.BLACK);
                }
            }
        }
    }


    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid);

        Label inputRows = new Label("Počet riadkov:");
        Label inputColumns = new Label("Počet stĺpcov:");
        Spinner<Integer> countRows = new Spinner<Integer>(10,60,30);
        Spinner<Integer> countColumns = new Spinner<Integer>(10,60,30);
        countRows.setMaxWidth(Double.MAX_VALUE);
        countColumns.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(countRows, Priority.ALWAYS);
        GridPane.setHgrow(countColumns, Priority.ALWAYS);
        Button btnOk = new Button("OK");

        grid.add(inputRows, 0,0);
        grid.add(inputColumns, 0,1);
        grid.add(countRows,1,0);
        grid.add(countColumns,1,1);
        grid.add(btnOk, 1,2);



        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 20, 10, 20));
        grid.setAlignment(Pos.CENTER);
        GridPane.setHalignment(btnOk, HPos.RIGHT);

        GridPane newGrid = new GridPane();
        VBox right = new VBox();
        right.setPadding(new Insets(10, 20, 10, 60));
        right.setSpacing(10);
        right.setAlignment(Pos.BOTTOM_LEFT);

        Button step = new Button("Krok");
        right.getChildren().add(step);
        step.setMaxWidth(Double.MAX_VALUE);
        step.setOnAction(event->{
            simulation();
        });

        Button startSim = new Button("Spusti");
        right.getChildren().add(startSim);
        startSim.setMaxWidth(Double.MAX_VALUE);

        Button stopSim = new Button("Skonci");
        right.getChildren().add(stopSim);
        stopSim.setMaxWidth(Double.MAX_VALUE);
        stopSim.setDisable(true);

        Button clear = new Button("Zmaz");
        right.getChildren().add(clear);
        clear.setMaxWidth(Double.MAX_VALUE);

        clear.setOnAction(event -> {
            for(List<Cell> list : matrix){
                for(Cell item : list){
                    item.setColor(Color.BLACK);
                    item.setIsLive(false);
                }
            }
        });

        BorderPane border = new BorderPane();
        border.setCenter(newGrid);
        border.setRight(right);
        Scene newScene = new Scene(border);

        btnOk.setOnAction(event -> {
            primaryStage.setScene(newScene);

            startGame(newScene,newGrid, countRows.getValue(),countColumns.getValue());
            primaryStage.sizeToScene();
        });

        AnimationTimer animationTimer = new AnimationTimer(){
            private long lastSimTime = 0;
            @Override
            public void handle(long now) {
                if (now - lastSimTime >= 550000000) {
                    simulation();
                    lastSimTime = now;
                }
            }
        };

        startSim.setOnAction(event -> {
            animationTimer.start();
            startSim.setDisable(true);
            stopSim.setDisable(false);
        });

        stopSim.setOnAction(event -> {
            animationTimer.stop();
            startSim.setDisable(false);
            stopSim.setDisable(true);
        });


        primaryStage.setTitle("Game of life");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}