package com.uwsoft.editor.gdx.ui.properties;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.uwsoft.editor.data.manager.DataManager;
import com.uwsoft.editor.gdx.stage.SandboxStage;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.IBaseItem;
import com.uwsoft.editor.renderer.actor.LabelItem;

import java.io.File;

public class LabelItemProperties extends PropertyBox implements IPropertyBox<LabelItem> {

    private final SandboxStage sandboxStage;
    private TextField lblTxtBox;
    private SelectBox<String> selectBox;
    private SelectBox<String> alignSelectBox;
    private SelectBox<Integer> sizeSelectBox;
    private int labelDefaultSize 	= 1;
    private int labelDefaultAlign 	= Align.center;
    private Integer[] sizeArray = new Integer[]{10, 12, 14, 16, 18, 24, 28, 32, 36, 42, 48, 54, 60, 72, 78, 88, 84, 102};
    private String[] alignArray = new String[]{"center","left","right","bottom","top","top-left","top-right","bottom-right","bottom-left"};

    public LabelItemProperties(SandboxStage sandboxStage, SceneLoader scene) {
        super(scene, "LabelItemProperties");
        this.sandboxStage = sandboxStage;
    }

    @Override
    public void setObject(LabelItem object) {
        item = object;


        lblTxtBox = ui.getTextBoxById("labelText");
        lblTxtBox.setText(((LabelItem) item).getDataVO().text);

        selectBox = ui.getSelectBoxById("labelSelectBox");
        selectBox.setWidth(100);
        sizeSelectBox = ui.getSelectBoxById("labelSizeBox");
        sizeSelectBox.setWidth(50);
        alignSelectBox = ui.getSelectBoxById("labelAlignBox");
        alignSelectBox.setWidth(100);
        setSelectBoxContent(item);
        setListeners();
    }

    private void setSelectBoxContent(final IBaseItem item) {

        int index = 0;
        int selectedIndex = -1;
        int sizeIndex 	= labelDefaultSize;
        int alignIndex 	= labelDefaultAlign;
        File folder = new File(DataManager.getInstance().getFreeTypeFontPath());
        if (folder.exists()) {
            String[] strArray = new String[folder.listFiles().length];
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.getName().substring(0, fileEntry.getName().lastIndexOf('.')).equals(((LabelItem) item).getDataVO().style)) {
                    selectedIndex = index;
                }
                strArray[index] = fileEntry.getName().substring(0, fileEntry.getName().lastIndexOf('.'));
                index++;
            }
            selectBox.setItems(strArray);
            if (selectedIndex != -1) {
                selectBox.setSelectedIndex(selectedIndex);
            }
            selectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent arg0, Actor arg1) {
                    if (item != null) {
                        String choosenStyle = selectBox.getSelected().toString();
                        setStyleToLabel(item, choosenStyle, ((LabelItem) item).getDataVO().size);
                        sandboxStage.saveSceneCurrentSceneData();
                        sandboxStage.uiStage.getItemsBox().initContent();
                    }
                }
            });
        }
        //size items
        for (int i = 0; i < sizeArray.length; i++) {
        	if (sizeArray[i] == ((LabelItem) item).getDataVO().size) {
        		sizeIndex = i;
        	}
        	
        }
        sizeSelectBox.setItems(sizeArray);
        if (sizeIndex != labelDefaultSize) {
        	sizeSelectBox.setSelectedIndex(sizeIndex);
        } else {
        	sizeSelectBox.setSelectedIndex(labelDefaultSize);
        }
        
        sizeSelectBox.addListener(new ChangeListener() {
        	@Override
        	public void changed(ChangeEvent arg0, Actor arg1) {
        		if (item != null) {
        			Integer choosenSize = sizeSelectBox.getSelected();
        			setStyleToLabel(item, ((LabelItem) item).getDataVO().style, choosenSize);
        			sandboxStage.saveSceneCurrentSceneData();
        			sandboxStage.uiStage.getItemsBox().initContent();
        		}
        	}
        });
        //align Items
        for (int i = 0; i < alignArray.length; i++) {
        	if (getAlignInt(alignArray[i]) == ((LabelItem) item).getDataVO().align) {
        		alignIndex	=	i;
        	}
        }
        alignSelectBox.setItems(alignArray);
        if (alignIndex != labelDefaultAlign) {
        	alignSelectBox.setSelectedIndex(alignIndex);
        } else {
        	alignSelectBox.setSelectedIndex(labelDefaultAlign);
        }

        alignSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent arg0, Actor arg1) {
                if (item != null) {
                    Integer choosenAlign = getAlignInt(alignSelectBox.getSelected());
                    ((LabelItem) item).setAlign(choosenAlign);
                    ((LabelItem) item).renew();
                    sandboxStage.saveSceneCurrentSceneData();
                    sandboxStage.uiStage.getItemsBox().initContent();
                }
            }
        });
    }


    private int getAlignInt(String string) {
		if(string.equals("none")){
			return Align.center;
		}else if(string.equals("left")){
			return Align.left;
		}else if(string.equals("right")){
			return Align.right;
		}else if(string.equals("top")){
			return Align.top;
		}else if(string.equals("bottom")){
			return Align.bottom;
		}else if(string.equals("top-left")){
			return Align.top|Align.left;
		}else if(string.equals("top-right")){
			return Align.top|Align.right;
		}else if(string.equals("bottom-right")){
			return Align.bottom|Align.right;
		}else if(string.equals("bottom-left")){
			return Align.bottom|Align.left;
		}
		return 0;
	}

	protected void setStyleToLabel(IBaseItem item, String choosenStyle, Integer size) {
        //-------------------
        int labelSize;
        if (size == 0) {
            labelSize = sizeArray[labelDefaultSize];
        } else {
            labelSize = size;
        }
        ((LabelItem) item).setStyle(choosenStyle, labelSize);
        ((LabelItem) item).renew();
    }


    @Override
    public void updateView() {

    }

    private void setListeners() {
        lblTxtBox.addListener(new ClickListener() {
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == 66) {
                    // set item id to
                    String text = lblTxtBox.getText();
                    if (item != null) {
                        ((LabelItem) item).getDataVO().text = text;
                        ((LabelItem) item).renew();
                    }
                }
                return true;
            }
        });

    }

}