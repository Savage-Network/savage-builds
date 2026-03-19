/*
 * (c) 2026 Sparta Network. - zrfadev
 *
 * SpartaMC Builds - Cylinder (Cilindro) - FUNCIONALIDADE EXTRA (nao na ajuda)
 * Uso: /bs cylinder <bloco> <raio> <altura> [preenchido]
 * Desenha um cilindro vertical na posicao do jogador.
 * Exemplo: /bs cylinder stone 8 10 true
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco> <raio> <altura> [preenchido] - Desenha cilindro vertical";

context.checkArgs(3, 4, usage);

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : 0;
    return new BaseBlock(id, data);
}

var block = parseBlock(argv[1]);
var radius = parseInt(argv[2]);
var height = parseInt(argv[3]);
var filled = argv.length > 4 ? (argv[4].toLowerCase() === "true" || argv[4] === "1") : false;

if (radius < 1 || radius > 50) {
    player.printError("Raio deve ser entre 1 e 50.");
    throw "Raio invalido";
}
if (height < 1 || height > 128) {
    player.printError("Altura deve ser entre 1 e 128.");
    throw "Altura invalida";
}

var sess = context.remember();
var pos = player.getPosition();
var cx = pos.getBlockX();
var cy = pos.getBlockY();
var cz = pos.getBlockZ();

var affected = 0;
var radiusSq = radius * radius;

for (var y = cy; y < cy + height; y++) {
    for (var x = cx - radius; x <= cx + radius; x++) {
        for (var z = cz - radius; z <= cz + radius; z++) {
            var dx = x - cx;
            var dz = z - cz;
            var distSq = dx * dx + dz * dz;

            var place = false;
            if (filled) {
                place = distSq <= radiusSq;
            } else {
                var dist = Math.sqrt(distSq);
                place = (dist >= radius - 0.5 && dist <= radius + 0.5);
            }

            if (place) {
                sess.setBlock(new Vector(x, y, z), block);
                affected++;
            }
        }
    }
}

player.print("\u00A7aCilindro " + (filled ? "preenchido" : "oco") + " desenhado! " + affected + " blocos.");
