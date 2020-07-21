//
// MIT License
// 
// Copyright (c) 2020 minecraft.kiwi
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
// 
package at.joestr.cartjets.commands;

import at.joestr.cartjets.CartJetsPlugin;
import at.joestr.cartjets.configuration.CurrentEntries;
import at.joestr.cartjets.configuration.JenkinsUpdater.State;
import at.joestr.cartjets.utils.LocaleHelper;
import at.joestr.cartjets.utils.MessageHelper;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Joel
 */
public class CommandCartjetsUpdate implements TabExecutor {

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return Collections.emptyList();
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {    
    if (args.length != 0) {
      return false;
    }
    
    final Locale locale =
			sender instanceof Player
			? LocaleHelper.resolve(((Player) sender).getLocale())
			: Locale.ENGLISH;
    
    if (!(sender instanceof Player)) {
      new MessageHelper()
        .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER)
        .locale(locale)
        .receiver(sender)
        .send();
      return true;
    }
		
		new MessageHelper()
			.path(CurrentEntries.LANG_CMD_CARTJETS_UPDATE_ASYNCSTART)
			.locale(locale)
			.receiver(sender)
			.send();
		
		CartJetsPlugin.getInstance().getUpdater().checkForUpdate().whenCompleteAsync((optionalUpdate, error) -> {
			if (error != null) {
				new MessageHelper()
					.path(CurrentEntries.LANG_CMD_CARTJETS_UPDATE_ERROR)
					.locale(locale)
					.receiver(sender)
					.send();
				return;
			}
			
			if (optionalUpdate.equals(State.SUCCESS_UPTODATE)) {
				new MessageHelper()
					.path(CurrentEntries.LANG_CMD_CARTJETS_UPDATE_UPTODATE)
					.locale(locale)
					.receiver(sender)
					.send();
				return;
			}
			
			if (optionalUpdate.equals(State.SUCCESS_AVAILABLE)) {
				new MessageHelper()
					.path(CurrentEntries.LANG_CMD_CARTJETS_UPDATE_AVAILABLE)
					.locale(locale)
					.receiver(sender)
					.modify(msg -> msg.replace("%update$downloadUrl", "...."))
					.send();
				return;
			}
			
			if (optionalUpdate.equals(State.SUCCES_DOWNLOADED)) {
				new MessageHelper()
					.path(CurrentEntries.LANG_CMD_CARTJETS_UPDATE_DOWNLOADED)
					.locale(locale)
					.receiver(sender)
					.send();
			}
		});
		
		return true;
  } 
}
