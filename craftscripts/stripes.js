/*
 * (c) 2026 Savage Network. - zrfadev
 * SavageMC Builds - Stripes (Listras)
 * Uso: /bs stripes <bloco1> <bloco2> [largura] [eixo]
 *   largura = tamanho de cada listra (padrao: 1)
 *   eixo = x, z ou y (padrao: x)
 * Exemplo: /bs stripes 35:0 35:15 3 z
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco1> <bloco2> [largura] [x|z|y] - Listras alternadas";

context.checkArgs(2, 4, usage);

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : 0;
    return new BaseBlock(id, data);
}

var block1 = parseBlock(argv[1]);
var block2 = parseBlock(argv[2]);
var width = argv.length > 3 ? parseInt(argv[3]) : 1;
var axis = argv.length > 4 ? argv[4].toLowerCase() : "x";
if (width < 1) width = 1;

var sess = context.remember();
var region = context.getSession().getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var affected = 0;

for (var x = min.getBlockX(); x <= max.getBlockX(); x++) {
    for (var z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
        for (var y = min.getBlockY(); y <= max.getBlockY(); y++) {
            var coord;
            if (axis === "z") coord = z - min.getBlockZ();
            else if (axis === "y") coord = y - min.getBlockY();
            else coord = x - min.getBlockX();

            var stripe = Math.floor(coord / width);
            var block = stripe % 2 === 0 ? block1 : block2;
            sess.setBlock(new Vector(x, y, z), block);
            affected++;
        }
    }
}

player.print("\u00A7aListras aplicadas! " + affected + " blocos (largura " + width + ", eixo " + axis + ").");
