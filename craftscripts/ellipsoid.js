/*
 * (c) 2026 Savage Network. - zrfadev
 *
 * SavageMC Builds - Ellipsoid (Elipsoide)
 * Uso: /bs ellipsoid <bloco> <raio_x> <raio_y> <raio_z> [preenchido]
 * Desenha um elipsoide na posicao do jogador.
 * Exemplo: /bs ellipsoid 35:14 10 5 10 true
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco> <raio_x> <raio_y> <raio_z> [preenchido] - Desenha elipsoide";

context.checkArgs(4, 5, usage);

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : 0;
    return new BaseBlock(id, data);
}

var block = parseBlock(argv[1]);
var rx = parseFloat(argv[2]);
var ry = parseFloat(argv[3]);
var rz = parseFloat(argv[4]);
var filled = argv.length > 5 ? (argv[5].toLowerCase() === "true" || argv[5] === "1") : false;

if (rx < 1 || rx > 50 || ry < 1 || ry > 50 || rz < 1 || rz > 50) {
    player.printError("Raios devem ser entre 1 e 50.");
    throw "Raios invalidos";
}

var sess = context.remember();
var pos = player.getPosition();
var cx = pos.getBlockX();
var cy = pos.getBlockY();
var cz = pos.getBlockZ();

var affected = 0;
var ix = Math.ceil(rx);
var iy = Math.ceil(ry);
var iz = Math.ceil(rz);

for (var x = cx - ix; x <= cx + ix; x++) {
    for (var y = cy - iy; y <= cy + iy; y++) {
        for (var z = cz - iz; z <= cz + iz; z++) {
            var dx = (x - cx) / rx;
            var dy = (y - cy) / ry;
            var dz = (z - cz) / rz;
            var distSq = dx * dx + dy * dy + dz * dz;

            var place = false;
            if (filled) {
                place = distSq <= 1.0;
            } else {
                var dist = Math.sqrt(distSq);
                place = (dist >= 0.9 && dist <= 1.1);
            }

            if (place) {
                sess.setBlock(new Vector(x, y, z), block);
                affected++;
            }
        }
    }
}

player.print("\u00A7aElipsoide " + (filled ? "preenchido" : "oco") + " desenhado! " + affected + " blocos.");
