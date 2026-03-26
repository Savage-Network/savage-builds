/*
 * (c) 2026 Savage Network. - zrfadev
 * Savage Network - Software Proprietario
 * Copyright (c) 2026 Savage Network. Todos os direitos reservados.
 *
 * SavageMC Builds - Replace (Substituir) - FUNCIONALIDADE EXTRA (nao na ajuda)
 * Uso: /bs replace <bloco_origem> <bloco_destino>
 * Substitui todos os blocos do tipo origem pelo destino na selecao.
 * Exemplo: /bs replace 1 4  (pedra por cobblestone)
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco_origem> <bloco_destino> - Substitui blocos na selecao";

context.checkArgs(2, 2, usage);

function parseBlock(str) {
    var parts = str.split(":");
    var id = parseInt(parts[0]);
    var data = parts.length > 1 ? parseInt(parts[1]) : -1;
    return { id: id, data: data };
}

var from = parseBlock(argv[1]);
var to = parseBlock(argv[2]);
var toBlock = new BaseBlock(to.id, to.data >= 0 ? to.data : 0);

var sess = context.remember();
var region = context.getSession().getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var affected = 0;

for (var x = min.getBlockX(); x <= max.getBlockX(); x++) {
    for (var y = min.getBlockY(); y <= max.getBlockY(); y++) {
        for (var z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
            var v = new Vector(x, y, z);
            var b = sess.getBlock(v);
            var match = (b.getId() === from.id);
            if (from.data >= 0) match = match && (b.getData() === from.data);
            if (match) {
                sess.setBlock(v, toBlock);
                affected++;
            }
        }
    }
}

player.print("\u00A7aSubstituicao concluida! " + affected + " blocos alterados.");
