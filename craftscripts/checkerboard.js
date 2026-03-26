/*
 * (c) 2026 Savage Network. - zrfadev
 * SavageMC Builds - Checkerboard Pattern
 * Uso: /bs checkerboard <bloco1> <bloco2> [tamanho]
 * Exemplo: /bs checkerboard 35:0 35:15 2
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco1> <bloco2> [tamanho] - Padrao xadrez na selecao";

context.checkArgs(2, 3, usage);

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : 0;
    return new BaseBlock(id, data);
}

var block1 = parseBlock(argv[1]);
var block2 = parseBlock(argv[2]);
var size = argv.length > 3 ? parseInt(argv[3]) : 1;
if (size < 1) size = 1;

var sess = context.remember();
var region = context.getSession().getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var affected = 0;

for (var x = min.getBlockX(); x <= max.getBlockX(); x++) {
    for (var z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
        for (var y = min.getBlockY(); y <= max.getBlockY(); y++) {
            var cx = Math.floor((x - min.getBlockX()) / size);
            var cz = Math.floor((z - min.getBlockZ()) / size);
            var block = (cx + cz) % 2 === 0 ? block1 : block2;
            sess.setBlock(new Vector(x, y, z), block);
            affected++;
        }
    }
}

player.print("\u00A7aXadrez aplicado! " + affected + " blocos alterados (tamanho " + size + ").");
