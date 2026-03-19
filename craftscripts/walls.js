/*
 * (c) 2026 Sparta Network. - zrfadev
 * Sparta Network - Software Proprietario
 * Copyright (c) 2026 Sparta Network. Todos os direitos reservados.
 *
 * SpartaMC Builds - Walls (Paredes) - FUNCIONALIDADE EXTRA (nao na ajuda)
 * Uso: /bs walls <bloco>
 * Desenha apenas as 4 paredes laterais da selecao (sem teto e chao).
 * Exemplo: /bs walls stone
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco> - Desenha apenas as paredes da selecao";

context.checkArgs(1, 1, usage);

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : 0;
    return new BaseBlock(id, data);
}

var block = parseBlock(argv[1]);

var sess = context.remember();
var region = context.getSession().getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var minX = min.getBlockX(), maxX = max.getBlockX();
var minY = min.getBlockY(), maxY = max.getBlockY();
var minZ = min.getBlockZ(), maxZ = max.getBlockZ();

var affected = 0;

for (var x = minX; x <= maxX; x++) {
    for (var y = minY; y <= maxY; y++) {
        for (var z = minZ; z <= maxZ; z++) {
            var onX = (x === minX || x === maxX);
            var onZ = (z === minZ || z === maxZ);
            if (onX || onZ) {
                sess.setBlock(new Vector(x, y, z), block);
                affected++;
            }
        }
    }
}

player.print("\u00A7aParedes aplicadas! " + affected + " blocos.");
