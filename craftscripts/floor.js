/*
 * (c) 2026 Sparta Network. - zrfadev
 * Sparta Network - Software Proprietario
 * Copyright (c) 2026 Sparta Network. Todos os direitos reservados.
 *
 * SpartaMC Builds - Floor (Chao) - FUNCIONALIDADE EXTRA (nao na ajuda)
 * Uso: /bs floor <bloco>
 * Preenche apenas o chao (face inferior Y minimo) da selecao.
 * Exemplo: /bs floor grass
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco> - Preenche apenas o chao da selecao";

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
var minY = min.getBlockY();
var minZ = min.getBlockZ(), maxZ = max.getBlockZ();

var affected = 0;

for (var x = minX; x <= maxX; x++) {
    for (var z = minZ; z <= maxZ; z++) {
        sess.setBlock(new Vector(x, minY, z), block);
        affected++;
    }
}

player.print("\u00A7aChao preenchido! " + affected + " blocos.");
