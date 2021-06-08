package me.zeroX150.cornos.features.module.impl.external;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.MConfNum;
import me.zeroX150.cornos.etc.config.MConfToggleable;
import me.zeroX150.cornos.etc.helper.Renderer;
import me.zeroX150.cornos.etc.render.Notification;
import me.zeroX150.cornos.features.module.Module;
import me.zeroX150.cornos.features.module.ModuleType;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.*;

public class OreSim extends Module {

    private final HashMap<Long, HashMap<OreType, List<Vec3d>>> chunkRenderers = new HashMap<>();
    MConfToggleable diamond = new MConfToggleable("Diamond", true, "Render diamond positions");
    MConfToggleable redstone = new MConfToggleable("Redstone", false, "Render redstone positions");
    MConfToggleable gold = new MConfToggleable("Gold", false, "Render gold positions");
    MConfToggleable ancientDebris = new MConfToggleable("Ancient Debris", false, "Render ancient debris positions");
    MConfToggleable iron = new MConfToggleable("Iron", false, "Render iron positions");
    MConfToggleable emerald = new MConfToggleable("Emerald", false, "Render emerald positions");
    MConfToggleable lapis = new MConfToggleable("Lapis", false, "Render lapis positions");
    MConfToggleable coal = new MConfToggleable("Coal", false, "Render coal positions");
    MConfNum chunkRange = new MConfNum("Chunk Range", 5, 10, 0, "Range of chunks to render");
    private long worldSeed = 0L;

    public OreSim() {
        super("OreSim", "Worldseed + Math = Ores", ModuleType.WORLD);
        mconf.add(chunkRange);
        mconf.add(diamond);
        mconf.add(redstone);
        mconf.add(gold);
        mconf.add(ancientDebris);
        mconf.add(iron);
        mconf.add(emerald);
        mconf.add(lapis);
        mconf.add(coal);
    }

    @Override
    public void onRender(MatrixStack ms, float td) {
        if (worldSeed != 0) {
            assert Cornos.minecraft.player != null;
            int chunkX = Cornos.minecraft.player.getChunkPos().x;
            int chunkZ = Cornos.minecraft.player.getChunkPos().z;

            int rangeVal = (int) chunkRange.getValue();
            for (int range = 0; range <= rangeVal; range++) {
                for (int x = -range + chunkX; x <= range + chunkX; x++) {
                    renderChunk(x, chunkZ + range - rangeVal);
                }
                for (int x = (-range) + 1 + chunkX; x < range + chunkX; x++) {
                    renderChunk(x, chunkZ - range + rangeVal + 1);
                }
            }
        }
        super.onRender(ms, td);
    }

    private void renderChunk(int x, int z) {
        long chunkKey = (long) x + ((long) z << 32);

        if (chunkRenderers.containsKey(chunkKey)) {
            // renders rarest ores last so you can see them better
            if (this.coal.isEnabled())
                renderOre(chunkKey, OreType.COAL, 47, 44, 54);
            if (this.iron.isEnabled())
                renderOre(chunkKey, OreType.IRON, 235, 162, 94);
            if (this.redstone.isEnabled())
                renderOre(chunkKey, OreType.REDSTONE, 245, 7, 23);
            if (this.gold.isEnabled())
                renderOre(chunkKey, OreType.GOLD, 247, 229, 30);
            if (this.lapis.isEnabled())
                renderOre(chunkKey, OreType.LAPIS, 8, 26, 189);
            if (this.diamond.isEnabled())
                renderOre(chunkKey, OreType.DIAMOND, 33, 244, 255);
            if (this.ancientDebris.isEnabled()) {
                renderOre(chunkKey, OreType.LDEBRIS, 209, 27, 245);
                renderOre(chunkKey, OreType.SDEBRIS, 209, 27, 245);
            }
            if (this.emerald.isEnabled())
                renderOre(chunkKey, OreType.EMERALD, 27, 209, 45);
        }
    }

    private void renderOre(long chunkKey, OreType type, int r, int g, int b) {
        for (Vec3d pos : chunkRenderers.get(chunkKey).get(type)) {
            Renderer.renderBlockOutline(pos, new Vec3d(1, 1, 1), r, g, b, 255);
        }
    }

    @Override
    public void onEnable() {
        reload();
        if (worldSeed == 0) {
            Notification.create("OreSim", new String[]{"do .setseed [seed] to input", "your seed"}, 5000);
            setEnabled(false);
        }
        super.onEnable();
    }

    private void reload() {
        int renderdistance = MinecraftClient.getInstance().options.viewDistance;

        assert Cornos.minecraft.player != null;
        int playerChunkX = Cornos.minecraft.player.getChunkPos().x;
        int playerChunkZ = Cornos.minecraft.player.getChunkPos().z;

        for (int i = playerChunkX - renderdistance; i < playerChunkX + renderdistance; i++) {
            for (int j = playerChunkZ - renderdistance; j < playerChunkZ + renderdistance; j++) {
                doMathOnChunk(i, j);
            }
        }
    }

    public void doMathOnChunk(int chunkX, int chunkZ) {
        long chunkKey = (long) chunkX + ((long) chunkZ << 32);

        if (chunkRenderers.containsKey(chunkKey))
            return;
        chunkX = chunkX << 4;
        chunkZ = chunkZ << 4;

        ChunkRandom random = new ChunkRandom();
        HashMap<OreType, List<Vec3d>> h = new HashMap<>();
        WorldAccess worldAccess = Cornos.minecraft.world;

        long populationSeed = random.setPopulationSeed(worldSeed, chunkX, chunkZ);

        for (Ore ore : Ore.ORES) {

            ArrayList<Vec3d> ores = new ArrayList<>();

            int repeat = ore.repeat;
            int index = ore.index;

            if (ore.name == OreType.LDEBRIS || ore.name == OreType.SDEBRIS) {
                assert worldAccess != null;
                Identifier id = worldAccess.getRegistryManager().get(Registry.BIOME_KEY)
                        .getId(worldAccess.getBiomeAccess().getBiome(new BlockPos(chunkX,0,chunkZ)));
                if (id == null) {
                    Notification.create("OreSim", new String[]{"Error doing math", "Something went wrong, you may have", "some mods that mess with world generation"}, 8000);
                    this.setEnabled(false);
                    return;
                }
                String name = id.getPath();
                if (name.equals("warped_forest")) {
                    index = 13;
                } else if (name.equals("crimson_forest")) {
                    index = 12;
                }
                if (ore.name == OreType.SDEBRIS) {
                    index++;
                }
            }

            random.setDecoratorSeed(populationSeed, index, ore.step);

            if (ore.generatorType == Generator.EMERALD) {
                repeat = random.nextInt(3) + 6;
            }

            for (int i = 0; i < repeat; i++) {

                int x = random.nextInt(16) + chunkX;
                int z = random.nextInt(16) + chunkZ;
                int y;

                if (ore.isDepthAverage) {
                    // DepthAverage: maxY is spread and minY is baseline
                    y = random.nextInt(ore.maxY) + random.nextInt(ore.maxY) - ore.maxY + ore.minY;
                } else {
                    y = random.nextInt(ore.maxY - ore.minY) + ore.minY;
                }

                switch (ore.generatorType) {
                    case DEFAULT:
                        ores.addAll(generateNormal(worldAccess, random, new BlockPos(x, y, z), ore.size));
                        break;
                    case EMERALD:
                        ores.add(new Vec3d(x, y, z));
                        break;
                    case NO_SURFACE:
                        ores.addAll(generateHidden(random, new BlockPos(x, y, z), ore.size));
                        break;
                    default:
                        System.out.println(ore.name + " has some unknown generator. Fix it!");
                }
            }
            h.put(ore.name, ores);
        }
        chunkRenderers.put(chunkKey, h);
    }

    public void setWorldSeed(long seed) {
        this.worldSeed = seed;
        chunkRenderers.clear();
        if (this.isEnabled()) {
            reload();
        }
    }

    private ArrayList<Vec3d> generateNormal(WorldAccess worldAccess, Random random, BlockPos blockPos, int veinSize) {
        float f = random.nextFloat() * 3.1415927F;
        float g = (float) veinSize / 8.0F;
        int i = MathHelper.ceil(((float) veinSize / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d = (double) blockPos.getX() + Math.sin(f) * (double) g;
        double e = (double) blockPos.getX() - Math.sin(f) * (double) g;
        double h = (double) blockPos.getZ() + Math.cos(f) * (double) g;
        double j = (double) blockPos.getZ() - Math.cos(f) * (double) g;
        double l = (blockPos.getY() + random.nextInt(3) - 2);
        double m = (blockPos.getY() + random.nextInt(3) - 2);
        int n = blockPos.getX() - MathHelper.ceil(g) - i;
        int o = blockPos.getY() - 2 - i;
        int p = blockPos.getZ() - MathHelper.ceil(g) - i;
        int q = 2 * (MathHelper.ceil(g) + i);
        int r = 2 * (2 + i);

        // for (int s = n; s <= n + q; ++s) {
        // for (int t = p; t <= p + q; ++t) {
        // if(o <= worldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) {
        return this.generateVeinPart(random, veinSize, d, e, h, j, l, m, n, o, p, q, r);
        // }
        // }
        // }

        // return null;
    }

    private ArrayList<Vec3d> generateVeinPart(Random random, int veinSize, double startX, double endX, double startZ,
                                              double endZ, double startY, double endY, int x, int y, int z, int size, int i) {

        BitSet bitSet = new BitSet(size * i * size);
        // BlockPos.Mutable mutable = new BlockPos.Mutable();
        double[] ds = new double[veinSize * 4];

        ArrayList<Vec3d> poses = new ArrayList<>();

        int n;
        double p;
        double q;
        double r;
        double s;
        for (n = 0; n < veinSize; ++n) {
            float f = (float) n / (float) veinSize;
            p = MathHelper.lerp(f, startX, endX);
            q = MathHelper.lerp(f, startY, endY);
            r = MathHelper.lerp(f, startZ, endZ);
            s = random.nextDouble() * (double) veinSize / 16.0D;
            double m = ((double) (MathHelper.sin(3.1415927F * f) + 1.0F) * s + 1.0D) / 2.0D;
            ds[n * 4] = p;
            ds[n * 4 + 1] = q;
            ds[n * 4 + 2] = r;
            ds[n * 4 + 3] = m;
        }

        for (n = 0; n < veinSize - 1; ++n) {
            if (!(ds[n * 4 + 3] <= 0.0D)) {
                for (int o = n + 1; o < veinSize; ++o) {
                    if (!(ds[o * 4 + 3] <= 0.0D)) {
                        p = ds[n * 4] - ds[o * 4];
                        q = ds[n * 4 + 1] - ds[o * 4 + 1];
                        r = ds[n * 4 + 2] - ds[o * 4 + 2];
                        s = ds[n * 4 + 3] - ds[o * 4 + 3];
                        if (s * s > p * p + q * q + r * r) {
                            if (s > 0.0D) {
                                ds[o * 4 + 3] = -1.0D;
                            } else {
                                ds[n * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        for (n = 0; n < veinSize; ++n) {
            double u = ds[n * 4 + 3];
            if (!(u < 0.0D)) {
                double v = ds[n * 4];
                double w = ds[n * 4 + 1];
                double aa = ds[n * 4 + 2];
                int ab = Math.max(MathHelper.floor(v - u), x);
                int ac = Math.max(MathHelper.floor(w - u), y);
                int ad = Math.max(MathHelper.floor(aa - u), z);
                int ae = Math.max(MathHelper.floor(v + u), ab);
                int af = Math.max(MathHelper.floor(w + u), ac);
                int ag = Math.max(MathHelper.floor(aa + u), ad);

                for (int ah = ab; ah <= ae; ++ah) {
                    double ai = ((double) ah + 0.5D - v) / u;
                    if (ai * ai < 1.0D) {
                        for (int aj = ac; aj <= af; ++aj) {
                            double ak = ((double) aj + 0.5D - w) / u;
                            if (ai * ai + ak * ak < 1.0D) {
                                for (int al = ad; al <= ag; ++al) {
                                    double am = ((double) al + 0.5D - aa) / u;
                                    if (ai * ai + ak * ak + am * am < 1.0D) {
                                        int an = ah - x + (aj - y) * size + (al - z) * size * i;
                                        if (!bitSet.get(an)) {
                                            bitSet.set(an);
                                            // mutable.set(ah, aj, al);
                                            // if (config.target.test(worldAccess.getBlockState(mutable), random)) {
                                            poses.add(new Vec3d(ah, aj, al));
                                            // }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return poses;
    }

    private ArrayList<Vec3d> generateHidden(Random random, BlockPos blockPos, int size) {

        ArrayList<Vec3d> poses = new ArrayList<>();

        int i = random.nextInt(size + 1);

        for (int j = 0; j < i; ++j) {
            size = Math.min(j, 7);
            int x = this.randomCoord(random, size) + blockPos.getX();
            int y = this.randomCoord(random, size) + blockPos.getY();
            int z = this.randomCoord(random, size) + blockPos.getZ();
            poses.add(new Vec3d(x, y, z));
        }

        return poses;
    }

    // ====================================
    // Mojang code
    // ====================================

    private int randomCoord(Random random, int size) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float) size);
    }

    private enum OreType {
        DIAMOND, REDSTONE, GOLD, IRON, COAL, EMERALD, SDEBRIS, LDEBRIS, LAPIS
    }

    private enum Generator {
        DEFAULT, EMERALD, NO_SURFACE
    }

    private static class Ore {
        public static final ArrayList<Ore> ORES = new ArrayList<>(Arrays.asList(
                new Ore(OreType.DIAMOND, 9, 6, 0, 16, 8, OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, 1, false,
                        Generator.DEFAULT),
                new Ore(OreType.REDSTONE, 8, 6, 0, 16, 8, OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, 8, false,
                        Generator.DEFAULT),
                new Ore(OreType.GOLD, 7, 6, 0, 32, 9, OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, 2, false,
                        Generator.DEFAULT),
                new Ore(OreType.IRON, 6, 6, 0, 64, 9, OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, 20, false,
                        Generator.DEFAULT),
                new Ore(OreType.COAL, 5, 6, 0, 128, 17, OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, 20, false,
                        Generator.DEFAULT),
                new Ore(OreType.EMERALD, 14, 6, 4, 32, 1, new BlockMatchRuleTest(Blocks.STONE), 6 - 8, false,
                        Generator.EMERALD),
                new Ore(OreType.SDEBRIS, 15, 7, 8, 120, 2, OreFeatureConfig.Rules.BASE_STONE_NETHER, 1, false,
                        Generator.NO_SURFACE),
                new Ore(OreType.LDEBRIS, 15, 7, 16, 8, 3, OreFeatureConfig.Rules.BASE_STONE_NETHER, 1, true,
                        Generator.NO_SURFACE),
                new Ore(OreType.LAPIS, 10, 6, 16, 16, 7, OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, 1, true,
                        Generator.DEFAULT)));
        final OreType name;
        final int index;
        final int step;
        final int minY;
        final int maxY;
        final int size;
        final RuleTest replacable;
        final int repeat;
        final boolean isDepthAverage;
        final Generator generatorType;

        private Ore(OreType name, int index, int step, int minY, int maxY, int size, RuleTest replacable, int repeat,
                    boolean isDepthAverage, Generator generatorType) {
            this.name = name;
            this.index = index;
            this.step = step;
            this.minY = minY;
            this.maxY = maxY;
            this.size = size;
            this.replacable = replacable;
            this.repeat = repeat;
            this.isDepthAverage = isDepthAverage;
            this.generatorType = generatorType;
        }
    }
}
