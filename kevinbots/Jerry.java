package kevinbots;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

public class Jerry implements Bot {
	public static float mutationsStärke;
	public static Path modelPath = Paths.get("models", "final");

	public static final int seitenLänge; // links + rechts + mitte

	static int maxView = 5;
	static int maxRadarBounds = 11;
	static int minRadarBounds = 0;
	static float blockiert = -5;
	static float frei = 0;
	static float apfel = 5;
	



	static {
		seitenLänge = maxView + maxView + 1;
	}

	public float score;


	/**
	 * Inputs 
	 */
	float[] inputs = new float[seitenLänge * seitenLänge + 8]; // +4 wegen gegner und apfel

	
	public void setParams(float[][] params) {
		this.params = params;
	}

//	public float[][] params = {
//			{5.714601f, -6.2325068f, 0.62284905f, -0.26889515f, -5.0859003f, 0.25326663f, -1.7236278f, 2.1150649f, 4.376727f, 2.2312727f, 2.529761f, 3.740768f, 0.23379599f, -4.133226f, -5.693501f, -4.8098593f, -0.8198803f, 2.5644703f, -5.4370003f, 7.620546f, -2.0865889f, -0.52930903f, -0.025133697f, 5.1494293f, -3.0132987f, 1.9348636f, 3.4866078f, -2.3720963f, -2.7113225f, 7.1511087f, 7.591416f, -2.5943537f, -1.7189404f, -3.1661527f, -2.336593f, 6.6476912f, -2.5304344f, -3.664051f, -0.031329747f, 3.0266f, 0.76604664f, -0.87110215f, 4.1313763f, -0.27754757f, -0.5562486f, -0.45353732f, 6.3903975f, 2.811633f, 1.5007657f, -5.1650805f, -2.2171645f, 5.481061f, -3.7637444f, -8.603648f, -0.41008183f, 2.6933382f, 1.2605004f, 2.4934332f, -3.1573753f, 5.2335935f, -1.5882376f, -3.985932f, 3.6676478f, -0.24155504f, 3.6840909f, -6.3272204f, -3.9715395f, 0.099843174f, -2.5936875f, -4.0423183f, 0.06323146f, 11.35324f, 2.3205032f, 0.80784184f, -1.3987043f, -0.84069407f, -2.7508638f, 2.5991278f, 2.3052268f, 2.3245034f, -5.8882127f, -6.9216647f, 2.1496894f, 3.3842585f, 1.2056757f, -1.0506514f, -1.2104186f, 0.7831165f, -0.9486913f, -5.4133587f, -2.1378846f, -0.7339817f, 0.80296993f, 2.6670463f, 1.0490973f, 6.3866553f, 2.0570955f, -6.8985343f, -6.789327f, -2.5990555f, 1.3973999f, 1.1786486f, 4.733213f, 3.3774567f, -0.29249296f, -2.98476f, -0.07633939f, -5.6332035f, 7.8332887f, 2.486023f, 0.1391383f, -2.2005274f, -0.11008324f, 5.412688f, -0.08113905f, 5.6074386f, -4.089549f, -2.685863f, 2.728683f, 2.4143214f, 0.2483057f, 0.18933491f, 1.924323f, 2.2194262f, -3.3723848f, 3.5533981f, 17.652819f, -2.080545f, 0.8484707f, 0.4833083f},
//			{4.521917f, 5.0774746f, 6.2969694f, 1.3320822f, 2.3594055f, -4.8818235f, 4.938528f, -4.4389586f, 2.9332314f, -0.27549654f, 1.0313855f, 5.0660353f, -10.633856f, 5.810065f, 5.23441f, -8.141044f, -2.0139399f, 1.4629577f, 0.45140207f, 0.49569988f, 0.80172765f, -1.4250264f, -1.1786143f, 4.4111214f, -2.1353848f, 7.729729f, 0.08744219f, 1.1246293f, 3.896935f, -1.5153949f, 4.521814f, -6.217021f, 4.1164355f, -2.284159f, 3.2350001f, 1.1200322f, 1.5012006f, -4.3843236f, 4.469213f, 1.0783236f, -1.4457636f, -4.380698f, 2.108882f, 3.7730265f, 1.0993905f, 8.200293f, 2.549477f, 4.878722f, 3.9410026f, 8.924456f, -0.78011024f, 5.65688f, -0.30501238f, -2.4116604f, 3.2370968f, -3.8780448f, -2.2201936f, 0.1274298f, -1.6951956f, -1.8923507f, -3.2484348f, -4.6412306f, 5.4830847f, -2.1569107f, 1.2448394f, -2.3266075f, -8.453569f, 0.7186494f, -0.11554124f, 0.6296318f, 4.9833193f, 3.407541f, -3.2766035f, 1.4603871f, 1.967799f, 5.1513743f, -6.2340074f, -2.6044526f, 0.8504692f, -1.0476325f, 8.701098f, -5.478205f, -11.651382f, 1.3065897f, 0.50856876f, -1.9694948f, -4.260304f, -2.1308143f, 0.33852726f, -5.035649f, 6.12244f, -1.3117964f, -1.5992433f, 9.155854f, -1.114319f, 0.37856922f, -3.8050525f, -3.840021f, -1.9371144f, 3.38379f, -0.84156966f, 2.1942377f, 4.714592f, 2.5120404f, 8.142608f, -3.5342999f, -1.421477f, 4.0591974f, 8.646866f, -4.0842767f, -3.3031342f, 1.1006006f, -6.108811f, -7.5193396f, 7.9237943f, 6.148617f, 3.648945f, -3.2866726f, 1.8278269f, -5.9654207f, 0.9306455f, -6.5162745f, 5.5823164f, -5.1083417f, 1.8495618f, 3.3282127f, -15.827249f, -2.3064134f, -1.9960438f, -4.3386784f},
//			{9.405326f, 3.1852725f, 5.2047534f, -2.795729f, -3.9523907f, 3.7025087f, 7.143507f, -1.0307522f, 0.9137867f, -0.48983967f, 4.028531f, 3.695722f, -3.695513f, -0.37542704f, -4.3052816f, 3.2997622f, 5.0267954f, -4.8146906f, -5.6010633f, -0.842391f, 5.0880246f, -0.0447511f, 2.594816f, 1.8966029f, 5.8391533f, -3.2205224f, 6.9355183f, 1.4855006f, 1.9491992f, 1.3694195f, -1.3904089f, 0.36305994f, -1.5679587f, -1.1597098f, 3.6734576f, -2.757112f, 1.3568293f, -1.3928683f, -3.7959328f, 1.4418994f, -2.2782867f, -3.2699275f, -4.0420036f, -1.9014808f, 0.67096f, -1.5452663f, 4.0679455f, -5.1288276f, 13.353193f, -1.8072817f, 5.653692f, 0.28698108f, -0.35178027f, -2.3893297f, 3.9101608f, -3.5941885f, 2.0557783f, 0.4488196f, 3.469497f, 1.4527978f, -3.4701116f, -8.255653f, 4.7082634f, 6.3602223f, -2.0258448f, -3.490687f, 0.19746752f, -2.0995705f, -2.6120958f, 5.1913915f, 3.3174856f, -1.4495926f, -6.14707f, 2.2053368f, 7.348084f, 0.8141681f, -4.503219f, -3.4174113f, -0.93656445f, -0.9887497f, -2.732126f, 2.4934065f, -0.3053678f, 2.004851f, 2.3551342f, 4.9379687f, -2.2477186f, 2.7621372f, 2.739309f, 5.036129f, -7.025054f, -3.1610687f, -4.7135386f, 3.6018164f, 3.584245f, 0.88960165f, -2.018187f, 4.7240796f, -2.060648f, -6.98807f, -4.487851f, 1.3399665f, -2.4862852f, 5.762877f, -3.36808f, 4.9249225f, 0.5486663f, -6.1947856f, -0.9687263f, 11.827442f, 1.5063034f, -4.5835066f, -2.8915956f, -4.5682425f, -1.9367191f, -0.7442196f, -5.9069605f, -2.0774806f, -6.734179f, 5.999658f, -1.6029913f, -3.594796f, -6.0088696f, 2.3430176f, -6.3634715f, -16.43751f, 1.6467526f, -1.6406919f, -2.651158f, -3.9059386f},
//			{3.517894f, 4.341438f, 6.518649f, 6.2565985f, -10.080728f, 0.6428313f, 2.3376384f, -4.555082f, -2.299964f, 5.483201f, -2.7888777f, -4.009637f, -0.3887258f, -5.603805f, 6.061614f, -5.585156f, 1.5487736f, -1.8578802f, -0.21685164f, 3.3806458f, 8.316304f, 0.36726376f, -1.6421545f, 0.1255748f, -1.0898174f, -4.676742f, -0.38644192f, 0.799172f, -0.7162549f, 0.22367142f, -1.1202985f, 11.12874f, 2.1369653f, 0.4544951f, 2.7932057f, -0.13143067f, 4.8044705f, 0.3956191f, -5.5548477f, 9.750143f, -4.0168285f, 0.38625464f, -5.3939753f, 4.079256f, 4.0950727f, 1.6606684f, 3.8849442f, -0.7401278f, 1.9091375f, 1.450663f, 2.7584448f, 0.73384017f, -6.309344f, -3.9448056f, 6.4208255f, 2.1201355f, -0.12765518f, 1.2835162f, -4.2615657f, -6.298596f, -1.6394615f, 3.5178385f, 9.691644f, 0.5367116f, 1.8037912f, 0.6403737f, -5.8070116f, -2.9631352f, 1.2129394f, -4.7977915f, 1.0697681f, -2.3754072f, 3.0196612f, 1.2802539f, 5.145381f, -0.70884866f, -3.877483f, -4.129131f, -3.8201015f, 3.7918394f, 0.32623476f, -1.2448828f, 3.342537f, -1.2477072f, 7.989815f, 3.5476491f, -2.8016913f, 1.6486092f, -6.434254f, -2.5352666f, -1.7394972f, -3.3664577f, 7.260085f, -3.2684898f, -5.0781217f, 4.1819105f, 0.80591816f, -1.2801583f, -8.4363785f, -1.234788f, 1.1206942f, -1.8001207f, -0.48109537f, 3.3804374f, -0.6321908f, 1.8336377f, -0.63679934f, 2.3393893f, 0.48518646f, -3.2405565f, 3.8948362f, 0.39344454f, -3.0541792f, -6.943096f, 0.15014474f, 1.560594f, -9.14989f, -1.4092319f, -7.10439f, 0.2628758f, -3.0391946f, -9.50951f, -5.296904f, -0.2548837f, -0.7595601f, 18.265793f, 2.8036675f, 2.7915246f, -1.709312f, 0.3018019f}
//	};
	public float[][] params;

	public void save(File file) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(new FileOutputStream(file));
		writer.println(params.length + " " + params[1].length);
		for (float[] param : params) {
			for (float v : param) {
				writer.print(v);
				writer.print(", ");
			}
			writer.print("\n");
		}
		writer.close();
	}

	public Jerry(File paramFile) throws FileNotFoundException {
		Scanner scanner = new Scanner(paramFile);
		params = new float[scanner.nextInt()][scanner.nextInt()];
		scanner.nextLine();
		for (float[] param : params) {
			String[] values = scanner.nextLine().split(",");
			for (int i = 0; i < param.length; i++) {
				param[i] = Float.parseFloat(values[i]);
			}
		}
	}

	public Jerry(Jerry vater, Jerry mutter) {
		this.params = new float[vater.params.length][vater.params[0].length];

		for (int i = 0; i < this.params.length; i++) {
			// 4 mal ausgeführt
			for (int j = 0; j < this.params[0].length; j++) {
				double zufallszahl = Math.random();
				double mutationsOffset = ThreadLocalRandom.current().nextDouble(-mutationsStärke, mutationsStärke);
				if (zufallszahl >= 0.5)
					this.params[i][j] = (float) (mutter.params[i][j] + mutationsOffset);
				else
					this.params[i][j] = (float) (vater.params[i][j] + mutationsOffset);
			}

		}


	}

	/**
	 *
	 */

	public Jerry() {
		try {
			Scanner scanner = new Scanner("jerry.txt");
			params = new float[scanner.nextInt()][scanner.nextInt()];
			scanner.nextLine();
			for (float[] param : params) {
				String[] values = scanner.nextLine().split(",");
				for (int i = 0; i < param.length; i++) {
					param[i] = Float.parseFloat(values[i]);
				}
			}
		} catch (Exception e) {

			params = new float[4][inputs.length + 1];

			for (float[] param : params) {
				for (int i = 0; i < param.length; i++) {
					param[i] = (float) ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
				}
			}
		}
	}

	/**
	 *
	 * @param parent
	 * @param mutationsStärke
	 */
	public Jerry(Jerry parent, float mutationsStärke) {
		inputs = new float[parent.inputs.length];
		params = new float[parent.params.length][parent.params[0].length];

		for (int i = 0; i < params.length; i++) {
			for (int k = 0; k < params[i].length; k++) {
				params[i][k] = (float) (parent.params[i][k] + (mutationsStärke * ThreadLocalRandom.current().nextDouble(-.01, .01)));
			}
		}
	}


	/**
	 * Transformiert die Koordinaten vom globalen Koordinatensystem in das vom "radar". Der Radar ist eine Art minimap für die schlange, damit sie die nähere Umgebung wahrnehmen kann.
	 *
	 * @param snake   bestimmt das Zentrum vom radar.
	 * @param element ausgangskoordinaten für transformation
	 * @return neue Instanz.
	 */
	static Coordinate transformToRadar(final Snake snake, final Coordinate element) {
		Coordinate minimapCoordinates = new Coordinate(
				(maxView + (element.x - snake.getHead().x)),
				(maxView + (element.y - snake.getHead().y))
		);

		return minimapCoordinates;
	}

	@Override
	public boolean equals(Object o) {
		boolean ok;
		if (this == o) return true;
		if (!(o instanceof Jerry)) return false;

		for (int i = 0; i < this.params.length; i++)
			for (int j = 0; j < this.params[i].length; j++)
				if (this.params[i][j] != ((Jerry) o).params[i][j])
					return false;
		return true;
	}


	@Override
	public int hashCode() {
		int result = Arrays.hashCode(inputs);
		result = 31 * result + Arrays.hashCode(params);
		return result;
	}

	/**
	 * snake und element müssen selbes Koordinatensystem haben
	 *
	 * @param snake Koordinaten der Schlange
	 * @param element
	 * @return
	 */
	static boolean isVisible(Snake snake, Coordinate element) {
		int dist;
		dist = (snake.getHead().x - element.x);
		dist = abs(dist);
		if (dist > maxView)
			return false;
		dist = (snake.getHead().y - element.y);
		dist = abs(dist);
		if (dist > maxView)
			return false;
		return true;
	}


	@Override
	public Direction chooseDirection(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
		// mach aus parametern input für NN
		Arrays.fill(inputs, 0);
		/**
		 * relative Position vom Apfel zu unserer Schlange
		 */
		setElementOnRadar(inputs, snake, apple, apfel);

		setElementOnRadar(inputs, snake, snake.getHead(), blockiert);

		setElementOnRadar(inputs, snake, opponent.getHead(), blockiert);
		snake.body.forEach(bodyElement -> setElementOnRadar(inputs, snake, bodyElement, blockiert));

		setElementOnRadar(inputs, snake, opponent.getHead(), blockiert);
		opponent.body.forEach(bodyElement -> setElementOnRadar(inputs, snake, bodyElement, blockiert));

		/**
		 * Inputs von länge-5 bis länge -8 für Wanderkennung der Schlange
		 */
		inputs[inputs.length - 8] = ((snake.mazeSize.y - snake.getHead().y) == 0) ? -1 : 0;
		inputs[inputs.length - 7] = ((snake.mazeSize.x - snake.getHead().x) == 0) ? -1 : 0;
		inputs[inputs.length - 6] = (snake.getHead().y == 0) ? -1 : 0;
		inputs[inputs.length - 5] = (snake.getHead().x == 0) ? -1 : 0;

		/**
		 * Koordinaten für Schlange und Apfel
		 */
		inputs[inputs.length - 4] = (apple.x - snake.getHead().x); //x apfel
		inputs[inputs.length - 3] = (apple.y - snake.getHead().y);//y apfel
		inputs[inputs.length - 2] = (opponent.getHead().x - snake.getHead().x) / (float) snake.mazeSize.x; //x opponent
		inputs[inputs.length - 1] = (opponent.getHead().y - snake.getHead().y) / (float) snake.mazeSize.y;//y opponent

		float[] decisions = evaluate(inputs);


		int maxI = 0;
		for (int i = 0; i < decisions.length; i++) {
			if (decisions[i] >= decisions[maxI]) {
				maxI = i;
			}
		}

		float aktuelleDist = euklidDist(snake.getHead(), apple);

		Coordinate zielPosition = snake.getHead().moveTo(DIRECTIONS[maxI]);

		float zukunftDist = euklidDist(zielPosition, apple);

		score += aktuelleDist - zukunftDist;


		return DIRECTIONS[maxI];
	}

	float euklidDist(Coordinate a, Coordinate b) {
		float deltaX = a.x - b.x;
		float deltaY = a.y - b.y;

		double x2 = Math.pow(deltaX, 2);
		double y2 = Math.pow(deltaY, 2);

		return (float) Math.sqrt(x2 + y2);

	}

	public float[] evaluate(float[] inputs) {
		float[] results = new float[4];
		results[0] = 0;
		results[1] = 0;
		results[2] = 0;
		results[3] = 0;
		for (int i = 0; i < inputs.length; i++) {
			//results[0] += upParams[i] * inputs[i];
			results[0] += params[0][i] * inputs[i];
			results[1] += params[1][i] * inputs[i];
			results[2] += params[2][i] * inputs[i];
			results[3] += params[3][i] * inputs[i];
		}
		results[0] += params[0][inputs.length];
		results[1] += params[1][inputs.length];
		results[2] += params[2][inputs.length];
		results[3] += params[3][inputs.length];
		return results;
	}

	public void mutate(float mutationsStärke) {
		for (int i = 0; i < params.length; i++) {
			for (int j = 0; j < params.length; j++) {
				params[i][j] += ThreadLocalRandom.current().nextDouble(-.01, .01) * mutationsStärke;
			}
		}
	}

	private static int transform2dTo1d(Coordinate coordinate) {

		return ((seitenLänge * coordinate.y) + coordinate.x);
	}

	private static void setElementOnRadar(float[] inputs, Snake snake, Coordinate position, float value) {
		if (!isVisible(snake, position))
			return;

		Coordinate elementOnRadar = transformToRadar(snake, position);
		int index = transform2dTo1d(elementOnRadar);
		inputs[index] = value;
	}

	private static final Direction[] DIRECTIONS = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
}


