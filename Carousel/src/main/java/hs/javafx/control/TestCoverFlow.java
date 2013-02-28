package hs.javafx.control;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TestCoverFlow extends Application {

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    DirectoryChooser directoryChooser = new DirectoryChooser();

    directoryChooser.setTitle("Choose a directory with images");
    File dir = directoryChooser.showDialog(null);

    List<Image> images = new ArrayList<>();
    int fileCount = 0;

    for(File file : dir.listFiles()) {
      if(file.isFile()) {
        images.add(new Image(new FileInputStream(file)));
        if(fileCount++ > 50) {
          break;
        }
      }
    }

    BorderPane borderPane = new BorderPane();

    TreeView<ImageHandle> carousel = new TreeView<>();

    carousel.setMinWidth(500);
    carousel.setMinHeight(300);

    TreeItem<ImageHandle> root = new TreeItem<>();

    carousel.setRoot(root);
    carousel.setShowRoot(false);

    for(Image image : images) {
      root.getChildren().add(new TreeItem<>(new ImageHandle(new ImageView(image))));
    }

    carousel.setCellFactory(new Callback<TreeView<ImageHandle>, TreeCell<ImageHandle>>() {
      @Override
      public TreeCell<ImageHandle> call(final TreeView<ImageHandle> carousel) {
        TreeCell<ImageHandle> carouselCell = new TreeCell<ImageHandle>() {
          @Override
          protected void updateItem(ImageHandle item, boolean empty) {
            super.updateItem(item, empty);

            if(!empty) {
              ImageView image = item.getImage();
              image.setPreserveRatio(true);
//              image.fitWidthProperty().bind(carousel.maxCellWidthProperty());
//              image.fitHeightProperty().bind(carousel.maxCellHeightProperty());
              setGraphic(image);
            }
            else {
              setGraphic(null);
            }
          }
        };

        carouselCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        carouselCell.setDisclosureNode(new Rectangle(0,0,0,0));
        //carouselCell.setEffect(new Reflection());

        return carouselCell;
      }
    });


    Scene scene = new Scene(borderPane);

    scene.getStylesheets().add("Carousel.css");
    stage.setScene(scene);
    borderPane.setTop(carousel);
    stage.setWidth(1280);
    stage.setHeight(720);
    stage.show();

    RayCarouselSkin<ImageHandle> skin = (RayCarouselSkin<ImageHandle>)carousel.getSkin();

    fillOptionGridPane(skin);

    optionGridPane.setPadding(new Insets(20.0));

    borderPane.setBottom(optionGridPane);

  }

  private static class ImageHandle {
    private final ImageView imageView;

    public ImageHandle(ImageView imageView) {
      this.imageView = imageView;
    }

    public ImageView getImage() {
      return imageView;
    }
  }

  private GridPane optionGridPane = new GridPane();

  public void fillOptionGridPane(final RayCarouselSkin<?> skin) {
    addSlider(skin.cellAlignmentProperty(), "%4.2f", "Cell Alignment (0.0 - 1.0)", 0.0, 1.0, 0.1, "The vertical alignment of cells which donot utilize all of the maximum available height");
    addSlider(skin.radiusRatioProperty(), "%4.2f", "Radius Ratio (0.0 - 2.0)", 0.0, 2.0, 0.1, "The radius of the carousel expressed as the fraction of half the view's width");
    addSlider(skin.viewDistanceRatioProperty(), "%4.2f", "View Distance Ratio (0.0 - 4.0)", 0.0, 4.0, 0.1, "The distance of the camera expressed as a fraction of the radius of the carousel");
    addSlider(skin.densityProperty(), "%6.4f", "Cell Density (0.001 - 0.1)", 0.001, 0.1, 0.0025, "The density of cells in cells per pixel of view width");
    addSlider(skin.maxCellWidthProperty(), "%4.0f", "Maximum Cell Width (1 - 2000)", 1, 1000, 5, "The maximum width a cell is allowed to become");
    addSlider(skin.maxCellHeightProperty(), "%4.0f", "Maximum Cell Height (1 - 2000)", 1, 1000, 5, "The maximum height a cell is allowed to become");
    addSlider(skin.carouselViewFractionProperty(), "%4.2f", "Carousel View Fraction (0.0 - 1.0)", 0.0, 1.0, 0.1, "The portion of the carousel that is used for displaying cells");
    addSlider(skin.viewAlignmentProperty(), "%4.2f", "View Alignment (0.0 - 1.0)", 0.0, 1.0, 0.1, "The vertical alignment of the camera with respect to the carousel");

    optionGridPane.add(new HBox() {{
      setSpacing(20);
      getChildren().add(new CheckBox("Reflections?") {{
        setStyle("-fx-font-size: 16px");
        selectedProperty().bindBidirectional(skin.reflectionEnabledProperty());
      }});
      getChildren().add(new CheckBox("Clip Reflections?") {{
        setStyle("-fx-font-size: 16px");
        selectedProperty().bindBidirectional(skin.clipReflectionsProperty());
      }});
    }}, 2, row++);
  }

  private int row = 1;

  private void addSlider(final DoubleProperty property, final String format, String description, double min, double max, final double increment, String longDescription) {
    optionGridPane.add(new Label(description) {{
      setStyle("-fx-font-size: 16px");
    }}, 1, row);
    optionGridPane.add(new Slider(min, max, property.get()) {{
      setStyle("-fx-font-size: 16px");
      valueProperty().bindBidirectional(property);
      setBlockIncrement(increment);
      setMinWidth(400);
    }}, 2, row);
    optionGridPane.add(new Label() {{
      setStyle("-fx-font-size: 16px");
      textProperty().bind(property.asString(format));
    }}, 3, row);

    row++;

    optionGridPane.add(new Label(longDescription) {{
      setPadding(new Insets(0, 0, 5, 0));
    }}, 1, row, 3, 1);

    row++;
  }
}