package org.data7.bYD_WORLD_UTRAL.advs.tab0;

import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import org.data7.bYD_WORLD_UTRAL.advs.AdvancementTabNamespaces;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.multiParents.MultiParentsAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import org.bukkit.Material;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;;

public class Oak_sapling3 extends MultiParentsAdvancement  {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.tab0_NAMESPACE, "oak_sapling3");


  public Oak_sapling3(BaseAdvancement... parents) {
    super(KEY.getKey(), new AdvancementDisplay(Material.OAK_SAPLING, "紫砂", AdvancementFrameType.TASK, true, true, 2f, 0f , "首次使用suicide自杀"),1, parents );
  }
}