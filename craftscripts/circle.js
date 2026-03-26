/*
 * (c) 2026 Savage Network. - zrfadev
 * Savage Network - Software Proprietario
 * Copyright (c) 2026 Savage Network. Todos os direitos reservados.
 *
 * SavageMC Builds - Circle (Circulo)
 * Uso: /bs circle <bloco> <raio> [preenchido]
 * Desenha um circulo horizontal no chao na posicao do jogador.
 * preenchido: true = preenchido, false = apenas contorno (padrao)
 * Exemplo: /bs circle stone 15 true
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco> <raio> [preenchido] - Desenha circulo horizontal";

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

if (radius < 1 || radius > 100) {
    player.printError("Raio deve ser entre 1 e 100.");
    throw "Raio invalido";
}

var sess = context.remember();
var pos = player.getPosition();
var centerX = pos.getBlockX();
var centerY = pos.getBlockY();
var centerZ = pos.getBlockZ();

var affected = 0;
var radiusSq = radius * radius;

for (var x = centerX - radius; x <= centerX + radius; x++) {
    for (var z = centerZ - radius; z <= centerZ + radius; z++) {
        var dx = x - centerX;
        var dz = z - centerZ;
        var distSq = dx * dx + dz * dz;

        var place = false;
        if (filled) {
            place = distSq <= radiusSq;
        } else {
            var dist = Math.sqrt(distSq);
            place = (dist >= radius - 0.5 && dist <= radius + 0.5);
        }

        if (place) {
            sess.setBlock(new Vector(x, centerY, z), block);
            affected++;
        }
    }
}

player.print("\u00A7aCirculo " + (filled ? "preenchido" : "vazio") + " desenhado! " + affected + " blocos.");
