package fr.epicanard.globalmarketchest.gui.paginator;

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
  @Getter
  private int previousPos = -1;
  @Getter
  private int nextPos = -1;
  @Getter
  private int numPagePos = -1;

  public PaginatorConfig(int height, int width, int startPos, int prevPos, int nextPos, int numPagePos) throws InvalidPaginatorParameter {
    if (height < 1 || height > 6)
      throw new InvalidPaginatorParameter("Height");
    if (width < 1 || width > 9)
      throw new InvalidPaginatorParameter("Width");
    if (startPos % 9 + width > 9 || (startPos - startPos % 9) / 9 + height > 6)
      throw new InvalidPaginatorParameter("StartPos");
    if (prevPos >= 53)
      throw new InvalidPaginatorParameter("PreviousPos");
    if (nextPos >= 53)
      throw new InvalidPaginatorParameter("NextPos");
    this.height = height;
    this.width = width;
    this.startPos = startPos;
    this.limit = height * width;
    this.previousPos = prevPos;
    this.nextPos = nextPos;
    this.numPagePos = numPagePos;
  }

  /**
   * Create a new PaginatorConfig with same constructor value
   * @return PaginatorConfig
   */
  public PaginatorConfig duplicate() {
    try {
      return new PaginatorConfig(this.height, this.width, this.startPos, this.previousPos, this.nextPos, this.numPagePos);
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