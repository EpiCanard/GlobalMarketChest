package fr.epicanard.globalmarketchest.auctions;

import fr.epicanard.globalmarketchest.utils.LangUtils;
import lombok.Getter;

import java.util.Arrays;

public enum StatusAuction {
  EXPIRED(-1, "Expired"),
  IN_PROGRESS(0, "InProgress"),
  FINISHED(1,"Finished"),
  ABANDONED(2, "Abandoned")
  ;

  @Getter
  private Integer value;
  private String lang;


  StatusAuction(Integer value, String lang) {
    this.value = value;
    this.lang = lang;
  }

  /**
   * Get translate language for status
   * Path inside language file : States.<key>
   *
   * @return Translated lang
   */
  public String getLang() {
    return LangUtils.get("States." + this.lang);
  }

  /**
   * Get StatusAuction from ins status key
   *
   * @param status Status key
   * @return StatusAuction
   */
  public static StatusAuction getStatusAuction(Integer status) {
    return Arrays.stream(StatusAuction.values())
        .filter(state -> state.value == status)
        .findFirst()
        .orElse(StatusAuction.IN_PROGRESS);
  }
}
