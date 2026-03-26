/*
 * (c) 2026 Savage Network. - zrfadev
 * SavageMC Builds - Color Swap
 * Uso: /bs colorswap <cor_origem> <cor_destino>
 * Troca todas as cores em la, argila colorida, vidro colorido, paineis e carpete.
 * 
 * Cores (data values):
 *   0=branco, 1=laranja, 2=magenta, 3=azul_claro, 4=amarelo,
 *   5=verde_claro, 6=rosa, 7=cinza, 8=cinza_claro, 9=ciano,
 *   10=roxo, 11=azul, 12=marrom, 13=verde, 14=vermelho, 15=preto
 *
 * Exemplo: /bs colorswap 11 14  (troca azul por vermelho)
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<cor_origem 0-15> <cor_destino 0-15> - Troca cores em blocos coloridos";

context.checkArgs(2, 2, usage);

var fromColor = parseInt(argv[1]);
var toColor = parseInt(argv[2]);

if (fromColor < 0 || fromColor > 15 || toColor < 0 || toColor > 15) {
    player.printError("Cores devem estar entre 0 e 15.");
    throw "Cor invalida";
}

var colorNames = [
    "branco", "laranja", "magenta", "azul_claro", "amarelo",
    "verde_claro", "rosa", "cinza", "cinza_claro", "ciano",
    "roxo", "azul", "marrom", "verde", "vermelho", "preto"
];

// IDs de blocos que usam data value como cor
var colorBlockIds = [35, 159, 95, 160, 171]; // wool, stained clay, stained glass, stained glass pane, carpet

var sess = context.remember();
var region = context.getSession().getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var affected = 0;

for (var x = min.getBlockX(); x <= max.getBlockX(); x++) {
    for (var z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
        for (var y = min.getBlockY(); y <= max.getBlockY(); y++) {
            var pos = new Vector(x, y, z);
            var current = sess.getBlock(pos);
            var blockId = current.getId();
            var blockData = current.getData();

            var isColorBlock = false;
            for (var i = 0; i < colorBlockIds.length; i++) {
                if (blockId === colorBlockIds[i]) {
                    isColorBlock = true;
                    break;
                }
            }

            if (isColorBlock && blockData === fromColor) {
                sess.setBlock(pos, new BaseBlock(blockId, toColor));
                affected++;
            }
        }
    }
}

player.print("\u00A7aTroca de cor: " + colorNames[fromColor] + " -> " + colorNames[toColor]);
player.print("\u00A7a" + affected + " blocos alterados.");
