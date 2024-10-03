package org.sam.istoeproject;

import enums.Colors;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

import java.util.Objects;

public class Buttons {

    public static Button lateralMenuButton(String icon_path, String text){
        Button button = new Button(text);
        button.setPrefSize(250, 25);

        ImageView icon = new ImageView(new Image(Objects.requireNonNull(Buttons.class.getResourceAsStream(icon_path))));
        icon.setFitWidth(30);
        icon.setFitHeight(30);
        icon.setX(15);
        lateralMenuButtonStyle(button);

        button.setGraphic(icon);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setAlignment(Pos.BOTTOM_LEFT);
        button.setFont(Font.font("Inter", 16));

        button.setOnMouseEntered(event -> {
            lateralMenuButtonStyleMouseEntered(button);
            button.setCursor(Cursor.HAND);
        });

        button.setOnMouseExited(event -> {
            lateralMenuButtonStyle(button);
            button.setCursor(Cursor.DEFAULT);
        });
        return button;
    }

    public Button menuButton(String icon_path, String text, Colors color){
        Button button = new Button(text);
        button.setPrefSize(500, 50);

        ImageView icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(icon_path))));
        icon.setFitWidth(100);
        icon.setFitHeight(100);
        menuButtonStyle(button, color.getCode());

        button.setGraphic(icon);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setFont(Font.font("Inter", 16));

        button.setOnMouseEntered(event -> {
            menuButtonStyle(button, subcolor(color));
            button.setCursor(Cursor.HAND);
        });

        button.setOnMouseExited(event -> {
            menuButtonStyle(button, color.getCode());
            button.setCursor(Cursor.DEFAULT);
        });
        return button;
    }


    public static void lateralMenuButtonStyle(Button customButton){
        customButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-graphic-text-gap: 20px;"
        );
    }

    public static void lateralMenuButtonStyleMouseEntered(Button customButton){
        customButton.setStyle(
                "-fx-background-color: rgba(150, 156, 205, 0.67);" +
                        "-fx-background-radius: 100;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 20;" +
                        "-fx-graphic-text-gap: 30px;"
        );
    }

    public void menuButtonStyle(Button customButton, String color){
        customButton.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: white;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 3;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 55px;" +
                        "-fx-graphic-text-gap: 20px;"
        );
    }

    public static void buttonStyle(Button customButton, String color){
        customButton.setStyle(
                "-fx-background-color: " + color + ";" +  // Insert the color dynamically
                        "-fx-background-radius: 25;" +            // Rounded corners
                        "-fx-border-color: white;" +              // Border color
                        "-fx-border-radius: 50;" +                // Rounded border
                        "-fx-text-fill: white;" +                 // Text color
                        "-fx-font-size: 25px;" +                  // Font size
                        "-fx-padding: 5 10 5 10;"               // Padding around the button
        );
    }

    public static Button button(String icon_path, String text, Colors color){
        Button button = new Button(text);

        ImageView icon = new ImageView(new Image(Objects.requireNonNull(Buttons.class.getResourceAsStream(icon_path))));
        icon.setFitWidth(35);
        icon.setFitHeight(35);
        buttonStyle(button, color.getCode());

        button.setGraphic(icon);
        button.setFont(Font.font("Inter", 16));

        button.setOnMouseEntered(event -> {
            buttonStyle(button, subcolor(color));
            button.setCursor(Cursor.HAND);
        });

        button.setOnMouseExited(event -> {
            buttonStyle(button, color.getCode());
            button.setCursor(Cursor.DEFAULT);
        });

        return button;
    }

    public static String subcolor(Colors color){
        switch (color){
            case BLUE -> {
                return Colors.DARK_BLUE.getCode();
            }
            case GREEN -> {
                return Colors.DARK_GREEN.getCode();
            }
            default -> {
                return null;
            }
        }
    }


}
