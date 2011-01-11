package claro.catalog.manager.client.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

public interface Images extends ClientBundle {
  @Source("health-0.gif")
  ImageResource health0();

  @Source("health-1.gif")
  ImageResource health1();
  
  @Source("health-2.gif")
  ImageResource health2();
  
  @Source("health-3.gif")
  ImageResource health3();
  
  @Source("health-4.gif")
  ImageResource health4();

  @Source("bin_closed.png")
  ImageResource deleteImage();

  @Source("application_form_edit.png")
  ImageResource editImage();

  @Source("bin_closed.png")
	ImageResource removeImmediately();

  @Source("help.gif")
	ImageResource help();
  
  @Source("closeButton1.png")
  ImageResource clearImage1();

  @Source("closeButton2.png")
  ImageResource clearImage2();
  
  @Source("closeButton3.png")
  ImageResource clearImage3();
}
