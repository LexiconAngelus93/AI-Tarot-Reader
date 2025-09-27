package com.tarotreader.data.provider

import com.tarotreader.data.model.TarotCard
import com.tarotreader.data.model.TarotDeck
import com.tarotreader.data.model.TarotSpread
import com.tarotreader.data.model.SpreadPosition

object TarotDataProvider {
    
    fun getMajorArcanaCards(deckId: String): List<TarotCard> {
        return listOf(
            TarotCard(
                id = "0_${deckId}",
                name = "The Fool",
                uprightMeaning = "New beginnings, innocence, spontaneity",
                reversedMeaning = "Recklessness, taken advantage of, inconsideration",
                description = "The Fool represents new beginnings, having faith in the future, being inexperienced, not knowing what to expect, having beginner's luck, improvising, believing in yourself and your abilities, being carefree, and having a free spirit.",
                keywords = listOf("beginnings", "innocence", "spontaneity", "faith"),
                numerology = "0",
                element = null,
                astrology = "Uranus",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/RWS_Tarot_00_Fool.jpg/200px-RWS_Tarot_00_Fool.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "1_${deckId}",
                name = "The Magician",
                uprightMeaning = "Manifestation, resourcefulness, power",
                reversedMeaning = "Manipulation, poor planning, untapped talents",
                description = "The Magician represents manifestation, resourcefulness, and power. The Magician shows you that you have all the tools and resources you need to make your dreams come true. It is a card of action, desires, and manifestation.",
                keywords = listOf("manifestation", "power", "resourcefulness", "action"),
                numerology = "1",
                element = null,
                astrology = "Mercury",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/RWS_Tarot_01_Magician.jpg/200px-RWS_Tarot_01_Magician.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "2_${deckId}",
                name = "The High Priestess",
                uprightMeaning = "Intuition, unconscious knowledge, divine feminine",
                reversedMeaning = "Secrets, disconnected from intuition, withdrawal",
                description = "The High Priestess represents intuition, unconscious knowledge, and the divine feminine. She is the guardian of the unconscious mind and represents our ability to access hidden knowledge and intuition.",
                keywords = listOf("intuition", "mystery", "unconscious", "divine feminine"),
                numerology = "2",
                element = null,
                astrology = "Moon",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/RWS_Tarot_02_High_Priestess.jpg/200px-RWS_Tarot_02_High_Priestess.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "3_${deckId}",
                name = "The Empress",
                uprightMeaning = "Femininity, beauty, nature, abundance",
                reversedMeaning = "Creative block, dependence, emptiness",
                description = "The Empress represents femininity, beauty, nature, and abundance. She is the mother archetype, symbolizing fertility, nurturing, and unconditional love. The Empress connects us with our senses and the pleasures of life.",
                keywords = listOf("femininity", "abundance", "nurturing", "nature"),
                numerology = "3",
                element = "Earth",
                astrology = "Venus",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d2/RWS_Tarot_03_Empress.jpg/200px-RWS_Tarot_03_Empress.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "4_${deckId}",
                name = "The Emperor",
                uprightMeaning = "Authority, structure, control, fatherhood",
                reversedMeaning = "Tyranny, rigidity, coldness, domination",
                description = "The Emperor represents authority, structure, control, and fatherhood. He symbolizes the power of leadership, stability, and protection. The Emperor connects us with our ability to create order and structure in our lives.",
                keywords = listOf("authority", "structure", "control", "stability"),
                numerology = "4",
                element = "Fire",
                astrology = "Aries",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/48/RWS_Tarot_04_Emperor.jpg/200px-RWS_Tarot_04_Emperor.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "5_${deckId}",
                name = "The Hierophant",
                uprightMeaning = "Spiritual wisdom, religious beliefs, conformity",
                reversedMeaning = "Personal beliefs, freedom, breaking convention",
                description = "The Hierophant represents spiritual wisdom, religious beliefs, and conformity. He is the guardian of spiritual knowledge and traditions. The Hierophant connects us with our ability to find guidance and wisdom from established sources.",
                keywords = listOf("spiritual wisdom", "tradition", "conformity", "guidance"),
                numerology = "5",
                element = "Earth",
                astrology = "Taurus",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/RWS_Tarot_05_Hierophant.jpg/200px-RWS_Tarot_05_Hierophant.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "6_${deckId}",
                name = "The Lovers",
                uprightMeaning = "Love, harmony, relationships, values alignment",
                reversedMeaning = "Self-love, disharmony, imbalance, misalignment of values",
                description = "The Lovers card represents love, harmony, and relationships. It signifies a harmonious relationship or partnership, whether romantic or platonic. This card often appears when you are faced with a choice that involves your values or relationships.",
                keywords = listOf("love", "relationships", "harmony", "choices"),
                numerology = "6",
                element = "Air",
                astrology = "Gemini",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/RWS_Tarot_06_Lovers.jpg/200px-RWS_Tarot_06_Lovers.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "7_${deckId}",
                name = "The Chariot",
                uprightMeaning = "Control, willpower, victory, assertion",
                reversedMeaning = "Lack of control, aggression, obstacles",
                description = "The Chariot represents control, willpower, and victory. It signifies the ability to overcome obstacles through determination and focus. The Chariot connects us with our ability to take control of our lives and move forward with confidence.",
                keywords = listOf("control", "willpower", "victory", "determination"),
                numerology = "7",
                element = "Water",
                astrology = "Cancer",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9b/RWS_Tarot_07_Chariot.jpg/200px-RWS_Tarot_07_Chariot.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "8_${deckId}",
                name = "Strength",
                uprightMeaning = "Courage, persuasion, influence, compassion",
                reversedMeaning = "Self doubt, weakness, insecurity",
                description = "Strength represents courage, persuasion, and influence. It signifies the ability to handle situations with compassion and understanding rather than force. Strength connects us with our inner courage and the power of gentleness.",
                keywords = listOf("courage", "strength", "compassion", "influence"),
                numerology = "8",
                element = "Fire",
                astrology = "Leo",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/RWS_Tarot_08_Strength.jpg/200px-RWS_Tarot_08_Strength.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "9_${deckId}",
                name = "The Hermit",
                uprightMeaning = "Soul searching, introspection, inner guidance",
                reversedMeaning = "Isolation, loneliness, withdrawal",
                description = "The Hermit represents soul searching, introspection, and inner guidance. He signifies the need to look within for answers and guidance. The Hermit connects us with our ability to find wisdom through solitude and reflection.",
                keywords = listOf("introspection", "soul searching", "inner guidance", "solitude"),
                numerology = "9",
                element = "Earth",
                astrology = "Virgo",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/RWS_Tarot_09_Hermit.jpg/200px-RWS_Tarot_09_Hermit.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "10_${deckId}",
                name = "Wheel of Fortune",
                uprightMeaning = "Change, cycles, fate, turning point",
                reversedMeaning = "Bad luck, lack of control, clinging to control",
                description = "The Wheel of Fortune represents change, cycles, and fate. It signifies the turning points in life and the cyclical nature of existence. The Wheel connects us with our ability to adapt to change and embrace the flow of life.",
                keywords = listOf("change", "cycles", "fate", "destiny"),
                numerology = "10",
                element = "Fire",
                astrology = "Jupiter",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/28/RWS_Tarot_10_Wheel_of_Fortune.jpg/200px-RWS_Tarot_10_Wheel_of_Fortune.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "11_${deckId}",
                name = "Justice",
                uprightMeaning = "Fairness, truth, law, cause and effect",
                reversedMeaning = "Unfairness, dishonesty, lack of accountability",
                description = "Justice represents fairness, truth, and law. It signifies the need for balance and the principle of cause and effect. Justice connects us with our ability to make fair decisions and take responsibility for our actions.",
                keywords = listOf("fairness", "justice", "truth", "balance"),
                numerology = "11",
                element = "Air",
                astrology = "Libra",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/RWS_Tarot_11_Justice.jpg/200px-RWS_Tarot_11_Justice.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "12_${deckId}",
                name = "The Hanged Man",
                uprightMeaning = "Suspension, restriction, letting go, sacrifice",
                reversedMeaning = "Martyrdom, indecision, delay, unnecessary sacrifice",
                description = "The Hanged Man represents suspension, restriction, and letting go. He signifies the need to pause and look at situations from a different perspective. The Hanged Man connects us with our ability to sacrifice and gain wisdom through surrender.",
                keywords = listOf("suspension", "sacrifice", "perspective", "pause"),
                numerology = "12",
                element = "Water",
                astrology = "Neptune",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/RWS_Tarot_12_Hanged_Man.jpg/200px-RWS_Tarot_12_Hanged_Man.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "13_${deckId}",
                name = "Death",
                uprightMeaning = "Endings, change, transformation, transition",
                reversedMeaning = "Resistance to change, fear of endings",
                description = "Death represents endings, change, and transformation. It signifies the end of one phase and the beginning of another. Death connects us with our ability to let go and embrace transformation.",
                keywords = listOf("endings", "transformation", "change", "transition"),
                numerology = "13",
                element = "Water",
                astrology = "Scorpio",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/54/RWS_Tarot_13_Death.jpg/200px-RWS_Tarot_13_Death.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "14_${deckId}",
                name = "Temperance",
                uprightMeaning = "Balance, moderation, patience, purpose",
                reversedMeaning = "Imbalance, excess, lack of harmony",
                description = "Temperance represents balance, moderation, and patience. It signifies the need to find harmony and blend opposing forces. Temperance connects us with our ability to find balance and moderation in all aspects of life.",
                keywords = listOf("balance", "moderation", "patience", "harmony"),
                numerology = "14",
                element = "Fire",
                astrology = "Sagittarius",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/RWS_Tarot_14_Temperance.jpg/200px-RWS_Tarot_14_Temperance.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "15_${deckId}",
                name = "The Devil",
                uprightMeaning = "Bondage, addiction, materialism, playfulness",
                reversedMeaning = "Release, independence, detachment",
                description = "The Devil represents bondage, addiction, and materialism. He signifies the chains that bind us and the temptations that lead us astray. The Devil connects us with our ability to break free from negative patterns and reclaim our power.",
                keywords = listOf("bondage", "addiction", "materialism", "temptation"),
                numerology = "15",
                element = "Earth",
                astrology = "Capricorn",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/RWS_Tarot_15_Devil.jpg/200px-RWS_Tarot_15_Devil.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "16_${deckId}",
                name = "The Tower",
                uprightMeaning = "Sudden change, upheaval, chaos, revelation",
                reversedMeaning = "Avoiding disaster, delaying necessary change",
                description = "The Tower represents sudden change, upheaval, and chaos. It signifies the collapse of structures that no longer serve us. The Tower connects us with our ability to rebuild after destruction and find truth in chaos.",
                keywords = listOf("upheaval", "sudden change", "destruction", "revelation"),
                numerology = "16",
                element = "Fire",
                astrology = "Mars",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/51/RWS_Tarot_16_Tower.jpg/200px-RWS_Tarot_16_Tower.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "17_${deckId}",
                name = "The Star",
                uprightMeaning = "Hope, faith, purpose, renewal",
                reversedMeaning = "Lack of faith, despair, discouragement",
                description = "The Star represents hope, faith, and renewal. It signifies the ability to find guidance and inspiration after difficult times. The Star connects us with our ability to heal and find hope in the future.",
                keywords = listOf("hope", "faith", "inspiration", "healing"),
                numerology = "17",
                element = "Air",
                astrology = "Aquarius",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/RWS_Tarot_17_Star.jpg/200px-RWS_Tarot_17_Star.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "18_${deckId}",
                name = "The Moon",
                uprightMeaning = "Illusion, fear, anxiety, subconscious",
                reversedMeaning = "Release of fear, repressed emotion, inner confusion",
                description = "The Moon represents illusion, fear, and the subconscious. It signifies the need to trust our intuition and navigate through uncertainty. The Moon connects us with our dreams, fears, and the hidden aspects of our psyche.",
                keywords = listOf("illusion", "fear", "subconscious", "intuition"),
                numerology = "18",
                element = "Water",
                astrology = "Pisces",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/RWS_Tarot_18_Moon.jpg/200px-RWS_Tarot_18_Moon.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "19_${deckId}",
                name = "The Sun",
                uprightMeaning = "Positivity, fun, warmth, success",
                reversedMeaning = "Temporary depression, lack of clarity",
                description = "The Sun represents positivity, warmth, and success. It signifies joy, vitality, and the power of light to dispel darkness. The Sun connects us with our ability to find happiness and embrace life with enthusiasm.",
                keywords = listOf("positivity", "joy", "success", "vitality"),
                numerology = "19",
                element = "Fire",
                astrology = "Sun",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/RWS_Tarot_19_Sun.jpg/200px-RWS_Tarot_19_Sun.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "20_${deckId}",
                name = "Judgement",
                uprightMeaning = "Reflection, reckoning, awakening, inner calling",
                reversedMeaning = "Lack of self-awareness, doubt, inner conflict",
                description = "Judgement represents reflection, reckoning, and awakening. It signifies the need to evaluate our actions and make important decisions. Judgement connects us with our ability to forgive, transform, and embrace our inner calling.",
                keywords = listOf("reflection", "awakening", "transformation", "forgiveness"),
                numerology = "20",
                element = "Water",
                astrology = "Pluto",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/RWS_Tarot_20_Judgement.jpg/200px-RWS_Tarot_20_Judgement.jpg",
                deckId = deckId
            ),
            TarotCard(
                id = "21_${deckId}",
                name = "The World",
                uprightMeaning = "Completion, integration, accomplishment, travel",
                reversedMeaning = "Seeking closure, short-term achievements",
                description = "The World represents completion, integration, and accomplishment. It signifies the end of a cycle and the achievement of goals. The World connects us with our ability to find wholeness and celebrate our accomplishments.",
                keywords = listOf("completion", "wholeness", "achievement", "journey"),
                numerology = "21",
                element = "Earth",
                astrology = "Saturn",
                cardImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/RWS_Tarot_21_World.jpg/200px-RWS_Tarot_21_World.jpg",
                deckId = deckId
            )
        )
    }
    
    fun getRiderWaiteDeck(): TarotDeck {
        return TarotDeck(
            id = "rider_waite",
            name = "Rider-Waite Deck",
            description = "The classic Tarot deck created by A.E. Waite and Pamela Colman Smith. Known for its detailed imagery and accessible symbolism, making it perfect for beginners and experienced readers alike.",
            coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/RWS_Tarot_00_Fool.jpg/300px-RWS_Tarot_00_Fool.jpg",
            cardBackImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/RWS_Tarot_00_Fool_%28back%29.jpg/200px-RWS_Tarot_00_Fool_%28back%29.jpg",
            numberOfCards = 78,
            deckType = com.tarotreader.data.model.DeckType.FULL_DECK
        )
    }
    
    fun getCelticCrossSpread(): TarotSpread {
        val positions = listOf(
            SpreadPosition("1_celtic_cross", "celtic_cross", 0, "Present Situation", "The core of the matter, what you're currently experiencing", 0.5f, 0.5f),
            SpreadPosition("2_celtic_cross", "celtic_cross", 1, "Challenge", "What blocks or challenges you in the present situation", 0.5f, 0.3f),
            SpreadPosition("3_celtic_cross", "celtic_cross", 2, "Distant Past", "The root cause or distant past influences", 0.3f, 0.5f),
            SpreadPosition("4_celtic_cross", "celtic_cross", 3, "Recent Past", "Recent events that led to the current situation", 0.7f, 0.5f),
            SpreadPosition("5_celtic_cross", "celtic_cross", 4, "Above", "Your conscious goals and ideals", 0.5f, 0.7f),
            SpreadPosition("6_celtic_cross", "celtic_cross", 5, "Below", "Your subconscious influences and hidden feelings", 0.5f, 0.1f),
            SpreadPosition("7_celtic_cross", "celtic_cross", 6, "Advice", "Guidance on how to approach the situation", 0.1f, 0.3f),
            SpreadPosition("8_celtic_cross", "celtic_cross", 7, "External Influences", "People or environments that influence the situation", 0.9f, 0.3f),
            SpreadPosition("9_celtic_cross", "celtic_cross", 8, "Hopes and Fears", "Your emotional state about the situation", 0.1f, 0.7f),
            SpreadPosition("10_celtic_cross", "celtic_cross", 9, "Outcome", "The likely outcome if you continue on your present path", 0.9f, 0.7f)
        )
        
        return TarotSpread(
            id = "celtic_cross",
            name = "Celtic Cross",
            description = "The most popular ten-card spread, offering comprehensive insight into any situation. Covers past, present, future, and various aspects of the querent's life.",
            positions = positions,
            imageUrl = "https://example.com/celtic-cross-spread.jpg",
            isCustom = false
        )
    }
    
    fun getThreeCardSpread(): TarotSpread {
        val positions = listOf(
            SpreadPosition("1_three_card", "three_card", 0, "Past", "What led you to this moment", 0.3f, 0.5f),
            SpreadPosition("2_three_card", "three_card", 1, "Present", "Your current situation", 0.5f, 0.5f),
            SpreadPosition("3_three_card", "three_card", 2, "Future", "What is likely to come", 0.7f, 0.5f)
        )
        
        return TarotSpread(
            id = "three_card",
            name = "Three Card Spread",
            description = "A simple yet powerful spread representing the past, present, and future. Perfect for gaining insight into a situation's progression.",
            positions = positions,
            imageUrl = "https://example.com/three-card-spread.jpg",
            isCustom = false
        )
    }
    
    fun getHorseshoeSpread(): TarotSpread {
        val positions = listOf(
            SpreadPosition("1_horseshoe", "horseshoe", 0, "Situation", "Your current situation", 0.5f, 0.1f),
            SpreadPosition("2_horseshoe", "horseshoe", 1, "Challenge", "Obstacles you face", 0.2f, 0.3f),
            SpreadPosition("3_horseshoe", "horseshoe", 2, "Past", "Recent past influences", 0.8f, 0.3f),
            SpreadPosition("4_horseshoe", "horseshoe", 3, "Below", "Subconscious influences", 0.3f, 0.6f),
            SpreadPosition("5_horseshoe", "horseshoe", 4, "Above", "Conscious goals", 0.7f, 0.6f),
            SpreadPosition("6_horseshoe", "horseshoe", 5, "Future", "Near future influences", 0.1f, 0.9f),
            SpreadPosition("7_horseshoe", "horseshoe", 6, "Outcome", "Final outcome if no action is taken", 0.9f, 0.9f)
        )
        
        return TarotSpread(
            id = "horseshoe",
            name = "Horseshoe Spread",
            description = "A seven-card spread that provides insight into the querent's current situation and possible outcomes. Cards are laid out in a horseshoe shape.",
            positions = positions,
            imageUrl = "https://example.com/horseshoe-spread.jpg",
            isCustom = false
        )
    }
    
    fun getDailyDrawSpread(): TarotSpread {
        val positions = listOf(
            SpreadPosition("1_daily_draw", "daily_draw", 0, "Daily Guidance", "Today's guidance and insight", 0.5f, 0.5f)
        )
        
        return TarotSpread(
            id = "daily_draw",
            name = "Daily Draw",
            description = "A single card draw for daily guidance and insight. Simple but effective for quick spiritual direction.",
            positions = positions,
            imageUrl = "https://example.com/daily-draw-spread.jpg",
            isCustom = false
        )
    }
}