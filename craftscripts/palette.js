/*
 * (c) 2026 Savage Network. - zrfadev
 * SavageMC Builds - Palette (Paleta de Cores)
 * Uso: /bs palette [wool|clay|glass]
 * Coloca uma parede com todas as 16 cores do tipo escolhido.
 * Exemplo: /bs palette clay
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "[wool|clay|glass] - Paleta de cores (padrao: wool)";

context.checkArgs(0, 1, usage);

var type = argv.length > 1 ? argv[1].toLowerCase() : "wool";

var blockId;
var typeName;
switch (type) {
    case "clay":
        blockId = 159;
        typeName = "Argila Colorida";
        break;
    case "glass":
        blockId = 95;
        typeName = "Vidro Colorido";
        break;
    default:
        blockId = 35;
        typeName = "La";
        break;
}

var colorNames = [
    "Branco", "Laranja", "Magenta", "Azul Claro", "Amarelo",
    "Verde Claro", "Rosa", "Cinza", "Cinza Claro", "Ciano",
    "Roxo", "Azul", "Marrom", "Verde", "Vermelho", "Preto"
];

var sess = context.remember();
var pos = player.getPosition();
var yaw = player.getYaw();

var dirX = 0, dirZ = 0;
if (yaw >= -45 && yaw < 45) { dirX = 1; }
else if (yaw >= 45 && yaw < 135) { dirZ = 1; }
else if (yaw >= 135 || yaw < -135) { dirX = -1; }
else { dirZ = -1; }

var perpX = -dirZ;
var perpZ = dirX;

var affected = 0;
var startX = pos.getBlockX() + dirX * 2;
var startY = pos.getBlockY();
var startZ = pos.getBlockZ() + dirZ * 2;

for (var i = 0; i < 16; i++) {
    var col = i % 8;
    var row = Math.floor(i / 8);

    var bx = startX + perpX * col;
    var by = startY + 2 - row;
    var bz = startZ + perpZ * col;

    sess.setBlock(new Vector(bx, by, bz), new BaseBlock(blockId, i));
    affected++;
}

player.print("\u00A7aPaleta de " + typeName + " colocada! (" + affected + " blocos)");
player.print("\u00A77Cores: 0-7 na linha de cima, 8-15 na linha de baixo");
for (var i = 0; i < 16; i++) {
    player.print("\u00A7e" + i + "\u00A77 = " + colorNames[i]);
}
