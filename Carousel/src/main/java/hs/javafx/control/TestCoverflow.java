package hs.javafx.control;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TestCoverflow extends Application {
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

    Carousel<ImageHandle> carousel = new Carousel<>();

    carousel.setMinWidth(500);
    carousel.setMinHeight(300);
    carousel.setMouseTransparent(false);
    carousel.setPickOnBounds(true);
    carousel.setFocusTraversable(true);

    for(Image image : images) {
      carousel.itemsProperty().get().add(new ImageHandle(new ImageView(image)));
    }

    carousel.cellFactoryProperty().set(new Callback<Carousel<ImageHandle>, CarouselCell<ImageHandle>>() {
      @Override
      public CarouselCell<ImageHandle> call(final Carousel<ImageHandle> carousel) {
        CarouselCell<ImageHandle> carouselCell = new CarouselCell<ImageHandle>() {
          @Override
          protected void updateItem(ImageHandle item, boolean empty) {
            super.updateItem(item, empty);

            if(!empty) {
              ImageView image = item.getImage();
              image.setPreserveRatio(true);
              image.fitWidthProperty().bind(carousel.heightProperty().multiply(0.6));
              image.fitHeightProperty().bind(carousel.heightProperty().multiply(0.6));
              setGraphic(image);
            }
            else {
              setGraphic(null);
            }
          }
        };

        carouselCell.setEffect(new Reflection());

        return carouselCell;
      }
    });

    borderPane.setTop(carousel);
    borderPane.setBottom(createOptionGridPane(carousel));

    stage.setScene(new Scene(borderPane));
    stage.setWidth(800);
    stage.setHeight(600);
    stage.show();
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

  public static GridPane createOptionGridPane(Carousel<?> carousel) {
    final DoubleProperty alignment = new SimpleDoubleProperty(0.8);
    final BooleanProperty reflectionEnabled = new SimpleBooleanProperty(true);
    final BooleanProperty clipReflections = new SimpleBooleanProperty(true);
    final DoubleProperty fieldOfViewRatio = new SimpleDoubleProperty(0.5);
    final DoubleProperty radiusRatio = new SimpleDoubleProperty(0.5);
    final DoubleProperty viewDistanceRatio = new SimpleDoubleProperty(0.5);
    final DoubleProperty density = new SimpleDoubleProperty(0.01);
    final DoubleProperty cellSizeRatio = new SimpleDoubleProperty(0.6);

    GridPane gridPane = new GridPane();

    gridPane.add(new Label("Alignment (0.0 - 1.0)"), 1, 1);
    gridPane.add(new Slider(0.0, 1.0, 0.8) {{
      valueProperty().bindBidirectional(alignment);
      setBlockIncrement(0.1);
    }}, 2, 1);
    gridPane.add(new Label() {{
      textProperty().bind(alignment.asString("%4.2f"));
    }}, 3, 1);

    gridPane.add(new CheckBox("Reflection?") {{
      selectedProperty().bindBidirectional(reflectionEnabled);
    }}, 2, 2);

    gridPane.add(new CheckBox("Clip Reflections?") {{
      selectedProperty().bindBidirectional(clipReflections);
    }}, 2, 3);

    gridPane.add(new Label("fieldOfViewRatio (0.0 - 2.0)"), 1, 4);
    gridPane.add(new Slider(0.0, 2.0, 0.5) {{
      valueProperty().bindBidirectional(fieldOfViewRatio);
      setBlockIncrement(0.1);
    }}, 2, 4);
    gridPane.add(new Label() {{
      textProperty().bind(fieldOfViewRatio.asString("%4.2f"));
    }}, 3, 4);

    gridPane.add(new Label("radiusRatio (0.0 - 2.0)"), 1, 5);
    gridPane.add(new Slider(0.0, 2.0, 0.5) {{
      valueProperty().bindBidirectional(radiusRatio);
      setBlockIncrement(0.1);
    }}, 2, 5);
    gridPane.add(new Label() {{
      textProperty().bind(radiusRatio.asString("%4.2f"));
    }}, 3, 5);

    gridPane.add(new Label("viewDistanceRatio (0.0 - 4.0)"), 1, 6);
    gridPane.add(new Slider(0.0, 4.0, 0.5) {{
      valueProperty().bindBidirectional(viewDistanceRatio);
      setBlockIncrement(0.1);
    }}, 2, 6);
    gridPane.add(new Label() {{
      textProperty().bind(viewDistanceRatio.asString("%4.2f"));
    }}, 3, 6);

    gridPane.add(new Label("density (0.001 - 0.1)"), 1, 7);
    gridPane.add(new Slider(0.001, 0.1, 0.01) {{
      valueProperty().bindBidirectional(density);
      setBlockIncrement(0.0025);
    }}, 2, 7);
    gridPane.add(new Label() {{
      textProperty().bind(density.asString("%6.4f"));
    }}, 3, 7);

    gridPane.add(new Label("cellSizeRatio (0.1 - 1.0)"), 1, 8);
    gridPane.add(new Slider(0.1, 1.0, 0.6) {{
      valueProperty().bindBidirectional(cellSizeRatio);
      setBlockIncrement(0.05);
    }}, 2, 8);
    gridPane.add(new Label() {{
      textProperty().bind(cellSizeRatio.asString("%4.2f"));
    }}, 3, 8);

    carousel.cellAlignmentProperty().bind(alignment);
    carousel.reflectionEnabledProperty().bind(reflectionEnabled);
    carousel.clipReflectionsProperty().bind(clipReflections);
    carousel.fieldOfViewRatioProperty().bind(fieldOfViewRatio);
    carousel.radiusRatioProperty().bind(radiusRatio);
    carousel.viewDistanceRatioProperty().bind(viewDistanceRatio);
    carousel.densityProperty().bind(density);
    carousel.cellSizeRatioProperty().bind(cellSizeRatio);

    return gridPane;
  }
}
