/*
 * (c) 2026 Sparta Network. - zrfadev
 *
 * SpartaMC Builds - Pyramid (Piramide)
 * Uso: /bs pyramid <bloco> <tamanho> [preenchido]
 * Desenha uma piramide na posicao do jogador.
 * Exemplo: /bs pyramid 35:14 10 true
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco> <tamanho> [preenchido] - Desenha piramide";

context.checkArgs(2, 3, usage);

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : 0;
    return new BaseBlock(id, data);
}

var block = parseBlock(argv[1]);
var size = parseInt(argv[2]);
var filled = argv.length > 3 ? (argv[3].toLowerCase() === "true" || argv[3] === "1") : false;

if (size < 1 || size > 50) {
    player.printError("Tamanho deve ser entre 1 e 50.");
    throw "Tamanho invalido";
}

var sess = context.remember();
var pos = player.getPosition();
var cx = pos.getBlockX();
var cy = pos.getBlockY();
var cz = pos.getBlockZ();

var affected = 0;
var halfSize = Math.floor(size / 2);

for (var layer = 0; layer < size; layer++) {
    var layerHalf = Math.floor((size - layer) / 2);
    var minX = cx - layerHalf;
    var maxX = cx + layerHalf;
    var minZ = cz - layerHalf;
    var maxZ = cz + layerHalf;
    var y = cy + layer;

    for (var x = minX; x <= maxX; x++) {
        for (var z = minZ; z <= maxZ; z++) {
            var place = false;
            if (filled) {
                place = true;
            } else {
                place = (x === minX || x === maxX || z === minZ || z === maxZ);
            }
            if (place) {
                sess.setBlock(new Vector(x, y, z), block);
                affected++;
            }
        }
    }
}

player.print("\u00A7aPiramide " + (filled ? "preenchida" : "oca") + " desenhada! " + affected + " blocos.");
