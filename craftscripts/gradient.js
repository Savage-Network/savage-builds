/*
 * SpartaMC Builds - Gradient Fill
 * Uso: /bs gradient <bloco1> <bloco2> [v|h|d]
 *   v = vertical (padrao), h = horizontal (eixo X), d = profundidade (eixo Z)
 * Exemplo: /bs gradient 35:0 35:15 v
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco1> <bloco2> [v|h|d] - Gradiente entre dois blocos";

context.checkArgs(2, 3, usage);

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : 0;
    return new BaseBlock(id, data);
}

var block1 = parseBlock(argv[1]);
var block2 = parseBlock(argv[2]);
var dir = argv.length > 3 ? argv[3].toLowerCase() : "v";

var sess = context.remember();
var region = context.getSession().getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var affected = 0;

for (var x = min.getBlockX(); x <= max.getBlockX(); x++) {
    for (var z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
        for (var y = min.getBlockY(); y <= max.getBlockY(); y++) {
            var ratio;
            if (dir === "h") {
                var span = max.getBlockX() - min.getBlockX();
                ratio = span > 0 ? (x - min.getBlockX()) / span : 0;
            } else if (dir === "d") {
                var span = max.getBlockZ() - min.getBlockZ();
                ratio = span > 0 ? (z - min.getBlockZ()) / span : 0;
            } else {
                var span = max.getBlockY() - min.getBlockY();
                ratio = span > 0 ? (y - min.getBlockY()) / span : 0;
            }

            var block = Math.random() < ratio ? block2 : block1;
            sess.setBlock(new Vector(x, y, z), block);
            affected++;
        }
    }
}

player.print("\u00A7aGradiente aplicado! " + affected + " blocos alterados.");
