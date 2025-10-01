package org.data7.bYD_WORLD_UTRAL.advs.tab0;

import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import org.data7.bYD_WORLD_UTRAL.advs.AdvancementTabNamespaces;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import org.bukkit.Material;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import org.data7.bYD_WORLD_UTRAL.advs.AdvancementTabNamespaces;

public class Ender_pearl1 extends BaseAdvancement  {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.tab0_NAMESPACE, "ender_pearl1");


  public Ender_pearl1(Advancement parent) {
    super(KEY.getKey(), new AdvancementDisplay(Material.ENDER_PEARL, "曲率引擎！", AdvancementFrameType.TASK, true, true, 1f, 0f , "首次使用tpa指令扭曲时空！"), parent, 1);
  }
}