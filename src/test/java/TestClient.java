import com.oyasunadev.mcraft.client.core.MinecraftStandalone;


/**
 * Created with IntelliJ IDEA.
 * User: Oliver Yasuna
 * Date: 9/30/12
 * Time: 5:26 PM
 */
public class TestClient
{
	public static void main(String[] args)
	{
		new TestClient();
	}

	public TestClient()
	{
		new MinecraftStandalone().startMinecraft();
	}
}
