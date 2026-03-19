/*
 * (c) 2026 Sparta Network. - zrfadev
 * SpartaMC Builds - Terraform (Naturalizar Terreno)
 * Uso: /bs terraform [camadas]
 * Aplica camadas naturais ao terreno na selecao:
 *   - Topo: grama (2)
 *   - 3 camadas abaixo: terra (3)
 *   - Resto: pedra (1) com veios aleatorios
 * O parametro [camadas] define a espessura de terra (padrao: 3).
 * Exemplo: /bs terraform 4
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "[camadas_terra] - Naturaliza o terreno (grama + terra + pedra)";

context.checkArgs(0, 1, usage);

var dirtLayers = argv.length > 1 ? parseInt(argv[1]) : 3;
if (dirtLayers < 1) dirtLayers = 1;

var AIR = new BaseBlock(0);
var GRASS = new BaseBlock(2);
var DIRT = new BaseBlock(3);
var STONE = new BaseBlock(1);
var GRAVEL = new BaseBlock(13);
var ANDESITE = new BaseBlock(1, 5);
var GRANITE = new BaseBlock(1, 1);
var DIORITE = new BaseBlock(1, 3);

var sess = context.remember();
var region = context.getSession().getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var affected = 0;

for (var x = min.getBlockX(); x <= max.getBlockX(); x++) {
    for (var z = min.getBlockZ(); z <= max.getBlockZ(); z++) {

        var topY = -1;
        for (var y = max.getBlockY(); y >= min.getBlockY(); y--) {
            var cur = sess.getBlock(new Vector(x, y, z));
            if (cur.getId() !== 0) {
                topY = y;
                break;
            }
        }

        if (topY < min.getBlockY()) continue;

        for (var y = min.getBlockY(); y <= topY; y++) {
            var cur = sess.getBlock(new Vector(x, y, z));
            if (cur.getId() === 0) continue;

            var depth = topY - y;
            var newBlock;

            if (depth === 0) {
                newBlock = GRASS;
            } else if (depth <= dirtLayers) {
                newBlock = DIRT;
            } else {
                var r = Math.random();
                if (r < 0.03) newBlock = GRAVEL;
                else if (r < 0.08) newBlock = ANDESITE;
                else if (r < 0.13) newBlock = GRANITE;
                else if (r < 0.18) newBlock = DIORITE;
                else newBlock = STONE;
            }

            sess.setBlock(new Vector(x, y, z), newBlock);
            affected++;
        }
    }
}

player.print("\u00A7aTerraformacao aplicada! " + affected + " blocos naturalizados.");
player.print("\u00A77Camadas de terra: " + dirtLayers);
