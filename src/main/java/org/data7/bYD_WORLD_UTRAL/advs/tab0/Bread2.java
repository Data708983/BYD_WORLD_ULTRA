package org.data7.bYD_WORLD_UTRAL.advs.tab0;

import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import me.clip.placeholderapi.PlaceholderAPI;
import org.data7.bYD_WORLD_UTRAL.advs.AdvancementTabNamespaces;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import org.bukkit.Material;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;

public class Bread2 extends BaseAdvancement  {

  public static AdvancementKey KEY = new AdvancementKey(AdvancementTabNamespaces.tab0_NAMESPACE, "bread2");


  public Bread2(Advancement parent) {
    super(KEY.getKey(), new AdvancementDisplay(Material.BREAD, "繁育！", AdvancementFrameType.TASK, true, true, 1f, 1f , PlaceholderAPI.setPlaceholders(null,"首次发出爱心：%img_heart%")), parent, 1);
  }
}