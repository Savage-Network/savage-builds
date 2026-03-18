/*
 * Sparta Network - Software Proprietario
 * Copyright (c) 2026 Sparta Network. Todos os direitos reservados.
 *
 * SpartaMC Builds - Sphere (Esfera) - FUNCIONALIDADE EXTRA (nao na ajuda)
 * Uso: /bs sphere <bloco> <raio> [preenchido]
 * Desenha uma esfera 3D na posicao do jogador.
 * Exemplo: /bs sphere 35:14 10 true
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco> <raio> [preenchido] - Desenha esfera 3D";

context.checkArgs(2, 3, usage);

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : 0;
    return new BaseBlock(id, data);
}

var block = parseBlock(argv[1]);
var radius = parseInt(argv[2]);
var filled = argv.length > 3 ? (argv[3].toLowerCase() === "true" || argv[3] === "1") : false;

if (radius < 1 || radius > 50) {
    player.printError("Raio deve ser entre 1 e 50.");
    throw "Raio invalido";
}

var sess = context.remember();
var pos = player.getPosition();
var cx = pos.getBlockX();
var cy = pos.getBlockY();
var cz = pos.getBlockZ();

var affected = 0;
var radiusSq = radius * radius;

for (var x = cx - radius; x <= cx + radius; x++) {
    for (var y = cy - radius; y <= cy + radius; y++) {
        for (var z = cz - radius; z <= cz + radius; z++) {
            var dx = x - cx;
            var dy = y - cy;
            var dz = z - cz;
            var distSq = dx * dx + dy * dy + dz * dz;

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

player.print("\u00A7aEsfera " + (filled ? "preenchida" : "oca") + " desenhada! " + affected + " blocos.");
