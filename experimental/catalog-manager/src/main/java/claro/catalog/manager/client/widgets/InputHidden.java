package claro.catalog.manager.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.TextBoxBase;

public class InputHidden extends TextBoxBase {

  public InputHidden(String fieldName) {
    super(Document.get().createHiddenInputElement());
    setName(fieldName);
  }
}