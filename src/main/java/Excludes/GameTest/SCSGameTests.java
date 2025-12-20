package Excludes.GameTest;

import com.simibubi.create.infrastructure.gametest.CreateTestFunction;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import java.util.Collection;

@EventBusSubscriber()
public class SCSGameTests {
    public static final int ONE_MINUTE = 20 * 60;

    private static final Class<?>[] TESTS = {
            SCSItemHandlerTest.class,
            SCSFluidHandlerTest.class,
    };

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(SCSGameTests.class);
    }

    @GameTestGenerator
    public static Collection<TestFunction> generateTests(){
        return CreateTestFunction.getTestsFrom(TESTS);
    }
}
