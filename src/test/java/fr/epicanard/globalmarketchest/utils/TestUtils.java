package fr.epicanard.globalmarketchest.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.Utils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GlobalMarketChest.class})
public class TestUtils {
  
  @Before
  public void startup() {
    // GlobalMarketChest tmp = PowerMockito.mock(GlobalMarketChest.class);
    // Utils utils = PowerMockito.mock(Utils.class);
  }
  
  @Test
  public void testToColor() {
  }

  @Test
  public void toPos() {
    //Assert.assertEquals(Utils.toPos(1, 2), 19);
  }

  @Test
  public void testGetItemStack() {
  }

  @Test
  public void testSetItemStackMeta() {
  }

  @Test
  public void testSetItemStackMetaA() {
  }

  @Test
  public void testSetItemStackMetaB() {
  }

  @Test
  public void testSetItemStackMetaC() {
  }

  @Test
  public void testGetButton() {
  }
}
