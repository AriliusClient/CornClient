package me.zeroX150.cornos.etc.manager;

import me.zeroX150.cornos.Cornos;
import me.zeroX150.cornos.etc.config.Friend;
import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.NameProtect;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsManager {

    private final HashMap<String, Friend> friends;

    public FriendsManager() {
        friends = new HashMap<>();
    }

    public HashMap<String, Friend> getFriends() {
        return friends;
    }

    public String filterString(String text) {
        if (!ModuleRegistry.search(NameProtect.class).isEnabled()) {
            return text;
        }
        for (Friend friend : friends.values()) {
            text = text.replaceAll(friend.getRealName(), friend.getFakeName());
        }
        text = text.replaceAll(Cornos.minecraft.getSession().getUsername(), "You");
        return text;
    }

    public boolean filterOrderedText(OrderedText orderedText, CharacterVisitor visitor) {
        if (!ModuleRegistry.search(NameProtect.class).isEnabled()) {
            return orderedText.accept(visitor);
        }
        DaBabyVisitor daBabyVisitor = new DaBabyVisitor();
        orderedText.accept(daBabyVisitor);
        OrderedText newText = daBabyVisitor.toOrderedText();
        return newText.accept(visitor);
    }

    class DaBabyVisitor implements CharacterVisitor {
        StringBuilder stringBuilder = new StringBuilder();
        List<OrderedText> orderedTexts = new ArrayList<>();
        List<Style> styles = new ArrayList<>();

        @Override
        public boolean accept(int index, Style style, int codePoint) {
            stringBuilder.append(Character.toChars(codePoint));
            styles.add(style);
            return true;
        }

        @Override
        public String toString() {
            return stringBuilder.toString();
        }

        public OrderedText toOrderedText() {
            String string = filterString(toString());
            for (int i = 0; i < string.toCharArray().length; i++) {
                orderedTexts.add(OrderedText.styled(string.toCharArray()[i],
                        styles.get(MathHelper.clamp(i, 0, styles.size() - 1))));
            }
            return OrderedText.concat(orderedTexts);
        }
    }
}
