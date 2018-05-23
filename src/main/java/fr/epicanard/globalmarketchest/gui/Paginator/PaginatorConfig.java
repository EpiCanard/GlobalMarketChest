package fr.epicanard.globalmarketchest.gui.Paginator;

import fr.epicanard.globalmarketchest.exceptions.InvalidPaginatorParameter;
import lombok.Getter;

public class PaginatorConfig {
  @Getter
  private int limit = 0;
  @Getter
  private int height;
  @Getter
  private int width;
  @Getter
  private int startPos;
  @Getter
  private int page = 0;

  public PaginatorConfig(int height, int width, int startPos) throws InvalidPaginatorParameter {
    if (height < 1 || height > 6)
      throw new InvalidPaginatorParameter("height");
    if (width < 1 || width > 9)
      throw new InvalidPaginatorParameter("width");
    if (startPos % 9 + width > 9 || (startPos - startPos % 9) / 9 + height > 6)
      throw new InvalidPaginatorParameter("startPos");
    this.height = height;
    this.width = width;
    this.startPos = startPos;
    this.limit = height * width;
  }

  /**
   * Create a new PaginatorConfig with same constructor value
   * @return PaginatorConfig
   */
  public PaginatorConfig duplicate() {
    try {
      return new PaginatorConfig(this.height, this.width, this.startPos);
    } catch(InvalidPaginatorParameter e) {
      return null;
    }
  }

  /**
   * Increment the number of page
   */
  public int nextPage() {
    this.page +=1;
    return page;
  }

  /**
   * Decrement the number of page
   */
  public int previousPage() {
    if (this.page > 0)
      this.page -=1;
    return page;
  }

  /**
   * Give the start limit for database limit request
   */
  public int getStartLimit() {
    return this.page * limit;
  }
}