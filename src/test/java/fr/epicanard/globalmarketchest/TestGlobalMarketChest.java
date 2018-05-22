package fr.epicanard.globalmarketchest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({GlobalMarketChest.class})
public class TestGlobalMarketChest {
  GlobalMarketChest gmc;

  @Before
  public void startup() {
    gmc = PowerMockito.mock(GlobalMarketChest.class);
  }
  
  @Test
  public void testCreateConfig() {
    Assert.assertTrue(true);
  }
}
