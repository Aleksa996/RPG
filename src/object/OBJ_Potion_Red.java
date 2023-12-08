package object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Potion_Red extends Entity {

    GamePanel gp;

    public OBJ_Potion_Red(GamePanel gp) {
        super(gp);
        this.gp = gp;
        value = 5;
        type = type_consumable;
        name = "Red potion";
        down1 = setup("objects/potion_red",gp.tileSize,gp.tileSize);
        defenseValue = 3;
        description = "[" + name + "]\nMade to heal + " + value + ".";

    }

    public void use(Entity entity){
        gp.gameState = gp.dialougeState;
        gp.ui.currentDialouge = "You drink the " + name;
        entity.life += value;
        gp.playSE(2);
    }

}
