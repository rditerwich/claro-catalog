package claro.catalog.manager.client.taxonomy;

import java.util.ArrayList;
import java.util.Map.Entry;

import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.items.ItemPageModel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

public abstract class EnumEditor extends Composite {

  private ArrayList<Integer> enumIndexes = new ArrayList<Integer>();
  private ArrayList<SMap<String, String>> enumValues = new ArrayList<SMap<String,String>>();
  private VerticalPanel valuePanel;
  protected Anchor addValueLink;

  public EnumEditor() {
    // TODO Auto-generated constructor stub
    initWidget(new VerticalPanel() {{
      add(new Label("values:"));
      add(valuePanel = new VerticalPanel() {{
        setStyleName("enumValuePanel");
      }});
      add(addValueLink = new Anchor("add") {{
        setVisible(false);
      }});
    }});
  }
  
  public void render(final ItemPageModel model, final PropertyInfo property) {
    
    if (!addValueLink.isVisible()) {
      addValueLink.setVisible(true);
      addValueLink.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          enumIndexes.add(getMaxIndex() + 1);
          enumValues.add(SMap.<String, String>empty());
          notifyChanged(property);
        }
      });

    }
    
    enumIndexes.clear();
    enumValues.clear();
    for (Entry<Integer, SMap<String, String>> entry : CollectionUtil.toTreeMap(property.enumValues).entrySet()) {
      enumIndexes.add(entry.getKey());
      enumValues.add(entry.getValue());
    }
    
    for (int ie = 0; ie < enumIndexes.size(); ie++) {
      final int finalie = ie;
      while (valuePanel.getWidgetCount() > ie + 1) {
        valuePanel.remove(valuePanel.getWidgetCount() - 1);
      }
      while (valuePanel.getWidgetCount() <= ie) {
        valuePanel.add(new HorizontalPanel() {{
          add(new TextBox() {{
            setWidth("2em");
            addChangeHandler(new ChangeHandler() {
              public void onChange(ChangeEvent event) {
                boolean skipChanged = !getText().trim().equals("") && enumIndexes.get(finalie) == null && enumValues.get(finalie).isEmpty();
                try {
                  int index = Integer.parseInt(getText());
                  enumIndexes.set(finalie, index);
                  if (!skipChanged) {
                    notifyChanged(property);
                  }
                }
                catch(NumberFormatException e) {
                  setText("" + enumIndexes.get(finalie));
                }
              }
            });
          }});
          add(new TextBox() {{
            addChangeHandler(new ChangeHandler() {
              public void onChange(ChangeEvent event) {
                SMap<String, String> newValues;
                if (getText().trim().equals("")) {
                  newValues = enumValues.get(finalie).removeKey(model.getSelectedLanguage());
                } else {
                  newValues = enumValues.get(finalie).set(model.getSelectedLanguage(), getText());
                }
                enumValues.set(finalie, newValues);
                notifyChanged(property);
              }
            });
          }});
          add(new Anchor("remove") {{
            addClickHandler(new ClickHandler() {
              public void onClick(ClickEvent event) {
                enumIndexes.remove(finalie);
                enumValues.remove(finalie);
                notifyChanged(property);
              }
            });
          }
        });
        }});
      }
      HorizontalPanel panel = (HorizontalPanel) valuePanel.getWidget(ie);
      TextBox indexTextBox = (TextBox) panel.getWidget(0);
      indexTextBox.setValue("" + enumIndexes.get(ie));
      TextBox valueTextBox = (TextBox) panel.getWidget(1);
      valueTextBox.setValue(enumValues.get(ie).get(model.getSelectedLanguage()));

    }
  }

  private int getMaxIndex() {
    int maxIndex = 0;
    for (int i = 0; i < enumIndexes.size(); i++) {
      maxIndex = Math.max(maxIndex, enumIndexes.get(i));
    }
    return maxIndex;
  }
  
  protected void notifyChanged(PropertyInfo property) {
    SMap<Integer, SMap<String, String>> result = SMap.empty();
    for (int i = 0; i < enumIndexes.size(); i++) {
        result = result.add(enumIndexes.get(i), enumValues.get(i));
    }
    property.enumValues = result;
    changed(property);
  }

  abstract void changed(PropertyInfo property);
}
