package object;

import entity.Entity;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Chest extends Entity {
    public OBJ_Chest(GamePanel gp){
        super(gp);
        name = "Chest";
        image = setup("objects/door",gp.tileSize,gp.tileSize);
    }

}
