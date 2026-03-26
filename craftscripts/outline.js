/*
 * (c) 2026 Savage Network. - zrfadev
 * SavageMC Builds - Outline (Arestas)
 * Uso: /bs outline <bloco>
 * Desenha apenas as 12 arestas (bordas) da selecao cubica.
 * Exemplo: /bs outline 35:14
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var usage = "<bloco> - Desenha as arestas da selecao";

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
            var onY = (y === minY || y === maxY);
            var onZ = (z === minZ || z === maxZ);

            if ((onX && onY) || (onX && onZ) || (onY && onZ)) {
                sess.setBlock(new Vector(x, y, z), block);
                affected++;
            }
        }
    }
}

player.print("\u00A7aContorno aplicado! " + affected + " blocos nas arestas.");
