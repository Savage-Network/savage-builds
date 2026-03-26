/*
 * (c) 2026 Savage Network. - zrfadev
 * SavageMC Builds - Measure (Medir Selecao)
 * Uso: /bs measure
 * Mostra as dimensoes, volume e area da selecao atual.
 */

importPackage(Packages.com.sk89q.worldedit);
importPackage(Packages.com.sk89q.worldedit.blocks);

var session = context.getSession();
var region = session.getSelection(player.getWorld());
var min = region.getMinimumPoint();
var max = region.getMaximumPoint();

var sizeX = max.getBlockX() - min.getBlockX() + 1;
var sizeY = max.getBlockY() - min.getBlockY() + 1;
var sizeZ = max.getBlockZ() - min.getBlockZ() + 1;
var volume = sizeX * sizeY * sizeZ;
var areaBase = sizeX * sizeZ;
var areaTotal = 2 * (sizeX * sizeZ + sizeX * sizeY + sizeY * sizeZ);

player.print("\u00A76\u00A7l===== Medidas da Selecao =====");
player.print("\u00A7eTamanho: \u00A7f" + sizeX + " x " + sizeY + " x " + sizeZ);
player.print("\u00A7eLargura (X): \u00A7f" + sizeX + " blocos");
player.print("\u00A7eAltura  (Y): \u00A7f" + sizeY + " blocos");
player.print("\u00A7eProf.   (Z): \u00A7f" + sizeZ + " blocos");
player.print("\u00A7eVolume: \u00A7f" + volume + " blocos");
player.print("\u00A7eArea base: \u00A7f" + areaBase + " blocos");
player.print("\u00A7eArea total: \u00A7f" + areaTotal + " blocos");
player.print("\u00A7ePonto 1: \u00A7f" + min.getBlockX() + ", " + min.getBlockY() + ", " + min.getBlockZ());
player.print("\u00A7ePonto 2: \u00A7f" + max.getBlockX() + ", " + max.getBlockY() + ", " + max.getBlockZ());
