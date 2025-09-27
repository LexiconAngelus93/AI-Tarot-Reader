# Digital Tarot Reading Experience Implementation

## Overview

The Digital Tarot Reading Experience is a core feature of the app that allows users to perform Tarot readings digitally. This document outlines the implementation details for creating an immersive, intuitive, and visually appealing digital reading experience.

## User Flow

1. **Deck Selection**
   - User browses available Tarot decks
   - User selects a deck for the reading

2. **Spread Selection**
   - User browses predefined spreads
   - User selects a spread or chooses a custom spread

3. **Card Selection**
   - User shuffles the deck (virtual animation)
   - User selects cards from the shuffled deck

4. **Reading Presentation**
   - Selected cards are placed in the spread positions
   - Reading interpretation is generated and displayed

5. **Interaction & Exploration**
   - User can tap on cards for detailed meanings
   - User can save the reading to history
   - User can add personal notes

## Implementation Details

### 1. Deck Selection Module

```kotlin
class DeckSelectionFragment : Fragment() {
    private lateinit var viewModel: DeckSelectionViewModel
    private lateinit var deckAdapter: DeckAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(DeckSelectionViewModel::class.java)
        
        setupDeckCarousel()
        observeViewModel()
    }
    
    private fun setupDeckCarousel() {
        deckAdapter = DeckAdapter(
            onDeckSelected = { deck ->
                viewModel.selectDeck(deck)
            }
        )
        
        binding.deckCarousel.apply {
            adapter = deckAdapter
            // Set up carousel effects
            setPageTransformer { page, position ->
                val scaleFactor = 0.85f + (1 - abs(position)) * 0.15f
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                page.alpha = 0.5f + (1 - abs(position)) * 0.5f
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.decks.observe(viewLifecycleOwner) { decks ->
            deckAdapter.submitList(decks)
        }
        
        viewModel.selectedDeck.observe(viewLifecycleOwner) { deck ->
            // Update UI to show selected deck details
            binding.deckName.text = deck.name
            binding.deckDescription.text = deck.description
            binding.continueButton.isEnabled = true
        }
        
        binding.continueButton.setOnClickListener {
            viewModel.selectedDeck.value?.let { deck ->
                findNavController().navigate(
                    DeckSelectionFragmentDirections.actionToSpreadSelection(deck.id)
                )
            }
        }
    }
}

class DeckSelectionViewModel : ViewModel() {
    private val deckRepository = DeckRepository()
    
    private val _decks = MutableLiveData<List<Deck>>()
    val decks: LiveData<List<Deck>> = _decks
    
    private val _selectedDeck = MutableLiveData<Deck>()
    val selectedDeck: LiveData<Deck> = _selectedDeck
    
    init {
        viewModelScope.launch {
            _decks.value = deckRepository.getAllDecks()
            
            // Set default deck if available
            val defaultDeckId = userPreferencesRepository.getDefaultDeckId()
            if (defaultDeckId != null) {
                _selectedDeck.value = deckRepository.getDeckById(defaultDeckId)
            }
        }
    }
    
    fun selectDeck(deck: Deck) {
        _selectedDeck.value = deck
    }
}

class DeckAdapter(
    private val onDeckSelected: (Deck) -> Unit
) : ListAdapter<Deck, DeckViewHolder>(DeckDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val binding = ItemDeckBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeckViewHolder(binding, onDeckSelected)
    }
    
    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DeckViewHolder(
    private val binding: ItemDeckBinding,
    private val onDeckSelected: (Deck) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(deck: Deck) {
        binding.deckName.text = deck.name
        
        // Load deck cover image
        Glide.with(binding.root)
            .load(deck.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.deckCover)
        
        binding.root.setOnClickListener {
            onDeckSelected(deck)
        }
    }
}

class DeckDiffCallback : DiffUtil.ItemCallback<Deck>() {
    override fun areItemsTheSame(oldItem: Deck, newItem: Deck): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Deck, newItem: Deck): Boolean {
        return oldItem == newItem
    }
}
```

### 2. Spread Selection Module

```kotlin
class SpreadSelectionFragment : Fragment() {
    private lateinit var viewModel: SpreadSelectionViewModel
    private lateinit var spreadAdapter: SpreadAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val deckId = args.deckId
        
        viewModel = ViewModelProvider(this, SpreadSelectionViewModelFactory(deckId))
            .get(SpreadSelectionViewModel::class.java)
        
        setupSpreadGrid()
        observeViewModel()
        setupCustomSpreadButton()
    }
    
    private fun setupSpreadGrid() {
        spreadAdapter = SpreadAdapter(
            onSpreadSelected = { spread ->
                viewModel.selectSpread(spread)
            }
        )
        
        binding.spreadGrid.apply {
            adapter = spreadAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }
    
    private fun observeViewModel() {
        viewModel.spreads.observe(viewLifecycleOwner) { spreads ->
            spreadAdapter.submitList(spreads)
        }
        
        viewModel.selectedSpread.observe(viewLifecycleOwner) { spread ->
            // Update UI to show selected spread details
            binding.spreadName.text = spread.name
            binding.spreadDescription.text = spread.description
            binding.spreadCardCount.text = getString(R.string.card_count, spread.positions.size)
            binding.continueButton.isEnabled = true
            
            // Show spread preview
            binding.spreadPreview.setSpread(spread)
        }
        
        binding.continueButton.setOnClickListener {
            viewModel.selectedSpread.value?.let { spread ->
                findNavController().navigate(
                    SpreadSelectionFragmentDirections.actionToCardSelection(
                        deckId = viewModel.deckId,
                        spreadId = spread.id
                    )
                )
            }
        }
    }
    
    private fun setupCustomSpreadButton() {
        binding.customSpreadButton.setOnClickListener {
            findNavController().navigate(
                SpreadSelectionFragmentDirections.actionToCustomSpreadSelection(
                    deckId = viewModel.deckId
                )
            )
        }
    }
}

class SpreadSelectionViewModel(
    val deckId: Long
) : ViewModel() {
    private val spreadRepository = SpreadRepository()
    
    private val _spreads = MutableLiveData<List<Spread>>()
    val spreads: LiveData<List<Spread>> = _spreads
    
    private val _selectedSpread = MutableLiveData<Spread>()
    val selectedSpread: LiveData<Spread> = _selectedSpread
    
    init {
        viewModelScope.launch {
            // Get all default spreads and user's custom spreads
            val defaultSpreads = spreadRepository.getDefaultSpreads()
            val customSpreads = spreadRepository.getUserCustomSpreads()
            
            _spreads.value = defaultSpreads + customSpreads
            
            // Set default spread if available
            val defaultSpreadId = userPreferencesRepository.getDefaultSpreadId()
            if (defaultSpreadId != null) {
                _selectedSpread.value = spreadRepository.getSpreadById(defaultSpreadId)
            }
        }
    }
    
    fun selectSpread(spread: Spread) {
        _selectedSpread.value = spread
    }
}

class SpreadAdapter(
    private val onSpreadSelected: (Spread) -> Unit
) : ListAdapter<Spread, SpreadViewHolder>(SpreadDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpreadViewHolder {
        val binding = ItemSpreadBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpreadViewHolder(binding, onSpreadSelected)
    }
    
    override fun onBindViewHolder(holder: SpreadViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SpreadViewHolder(
    private val binding: ItemSpreadBinding,
    private val onSpreadSelected: (Spread) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(spread: Spread) {
        binding.spreadName.text = spread.name
        binding.cardCount.text = binding.root.context.getString(
            R.string.card_count,
            spread.positions.size
        )
        
        // Set up spread preview
        binding.spreadPreview.setSpread(spread)
        
        binding.root.setOnClickListener {
            onSpreadSelected(spread)
        }
    }
}

class SpreadPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val cardPlaceholderPaint = Paint().apply {
        color = Color.GRAY
        alpha = 100
        style = Paint.Style.FILL
    }
    
    private val cardOutlinePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private var spread: Spread? = null
    private val cardRects = mutableListOf<RectF>()
    
    fun setSpread(spread: Spread) {
        this.spread = spread
        calculateCardPositions()
        invalidate()
    }
    
    private fun calculateCardPositions() {
        cardRects.clear()
        
        val spread = this.spread ?: return
        
        // Calculate card size based on view size and number of cards
        val cardWidth = width * 0.2f
        val cardHeight = cardWidth * 1.5f // Standard tarot card ratio
        
        for (position in spread.positions) {
            val x = position.xPosition * width
            val y = position.yPosition * height
            val rotation = position.rotation
            
            // Create card rectangle
            val rect = RectF(
                x - cardWidth / 2,
                y - cardHeight / 2,
                x + cardWidth / 2,
                y + cardHeight / 2
            )
            
            // Apply rotation
            val matrix = Matrix()
            matrix.setRotate(rotation, x, y)
            
            // Add to list
            cardRects.add(rect)
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateCardPositions()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        for (rect in cardRects) {
            canvas.drawRoundRect(rect, 8f, 8f, cardPlaceholderPaint)
            canvas.drawRoundRect(rect, 8f, 8f, cardOutlinePaint)
        }
    }
}
```

### 3. Card Selection Module

```kotlin
class CardSelectionFragment : Fragment() {
    private lateinit var viewModel: CardSelectionViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val deckId = args.deckId
        val spreadId = args.spreadId
        
        viewModel = ViewModelProvider(
            this,
            CardSelectionViewModelFactory(deckId, spreadId)
        ).get(CardSelectionViewModel::class.java)
        
        setupCardSelectionView()
        observeViewModel()
    }
    
    private fun setupCardSelectionView() {
        binding.cardSelectionView.setOnCardSelectedListener { position ->
            viewModel.selectCard(position)
        }
        
        binding.reshuffleButton.setOnClickListener {
            viewModel.reshuffleCards()
        }
        
        binding.continueButton.setOnClickListener {
            viewModel.finalizeReading()
        }
    }
    
    private fun observeViewModel() {
        viewModel.deck.observe(viewLifecycleOwner) { deck ->
            binding.deckName.text = deck.name
            binding.cardSelectionView.setCardBackImage(deck.cardBackImageUrl)
        }
        
        viewModel.spread.observe(viewLifecycleOwner) { spread ->
            binding.spreadName.text = spread.name
            binding.cardCountText.text = getString(
                R.string.cards_to_select,
                0,
                spread.positions.size
            )
        }
        
        viewModel.shuffledDeck.observe(viewLifecycleOwner) { shuffledDeck ->
            binding.cardSelectionView.setShuffledDeck(shuffledDeck)
        }
        
        viewModel.selectedCardCount.observe(viewLifecycleOwner) { count ->
            binding.cardCountText.text = getString(
                R.string.cards_to_select,
                count,
                viewModel.spread.value?.positions?.size ?: 0
            )
            
            // Enable continue button when all cards are selected
            binding.continueButton.isEnabled = count == viewModel.spread.value?.positions?.size
        }
        
        viewModel.navigateToReading.observe(viewLifecycleOwner) { readingId ->
            if (readingId != null) {
                findNavController().navigate(
                    CardSelectionFragmentDirections.actionToReadingResult(readingId)
                )
            }
        }
    }
}

class CardSelectionViewModel(
    private val deckId: Long,
    private val spreadId: Long
) : ViewModel() {
    private val deckRepository = DeckRepository()
    private val spreadRepository = SpreadRepository()
    private val readingRepository = ReadingRepository()
    
    private val _deck = MutableLiveData<Deck>()
    val deck: LiveData<Deck> = _deck
    
    private val _spread = MutableLiveData<Spread>()
    val spread: LiveData<Spread> = _spread
    
    private val _shuffledDeck = MutableLiveData<List<Card>>()
    val shuffledDeck: LiveData<List<Card>> = _shuffledDeck
    
    private val selectedCards = mutableListOf<Card>()
    
    private val _selectedCardCount = MutableLiveData(0)
    val selectedCardCount: LiveData<Int> = _selectedCardCount
    
    private val _navigateToReading = MutableLiveData<Long?>()
    val navigateToReading: LiveData<Long?> = _navigateToReading
    
    init {
        viewModelScope.launch {
            _deck.value = deckRepository.getDeckById(deckId)
            _spread.value = spreadRepository.getSpreadById(spreadId)
            
            // Load and shuffle deck
            val cards = deckRepository.getCardsForDeck(deckId)
            shuffleDeck(cards)
        }
    }
    
    private fun shuffleDeck(cards: List<Card>) {
        val shuffled = cards.shuffled()
        _shuffledDeck.value = shuffled
        selectedCards.clear()
        _selectedCardCount.value = 0
    }
    
    fun reshuffleCards() {
        _shuffledDeck.value?.let { cards ->
            shuffleDeck(cards)
        }
    }
    
    fun selectCard(position: Int) {
        val cards = _shuffledDeck.value ?: return
        val spread = _spread.value ?: return
        
        if (selectedCards.size >= spread.positions.size) {
            // Already selected all cards
            return
        }
        
        if (position < 0 || position >= cards.size) {
            // Invalid position
            return
        }
        
        val card = cards[position]
        selectedCards.add(card)
        _selectedCardCount.value = selectedCards.size
    }
    
    fun finalizeReading() {
        viewModelScope.launch {
            val deck = _deck.value ?: return@launch
            val spread = _spread.value ?: return@launch
            
            if (selectedCards.size != spread.positions.size) {
                // Not all cards selected
                return@launch
            }
            
            // Create reading with random card orientations
            val readingCards = selectedCards.mapIndexed { index, card ->
                ReadingCard(
                    card = card,
                    position = spread.positions[index],
                    isReversed = Random.nextBoolean()
                )
            }
            
            // Generate interpretation
            val interpretation = interpretationGenerator.generateInterpretation(
                deck = deck,
                spread = spread,
                cards = readingCards
            )
            
            // Calculate eigenvalue
            val eigenvalue = calculateEigenvalue(readingCards.map { it.card })
            
            // Save reading
            val reading = Reading(
                deckId = deck.id,
                spreadId = spread.id,
                title = "Reading on ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())}",
                interpretation = interpretation.holisticReading,
                eigenvalue = eigenvalue.value,
                eigenvalueCardId = eigenvalue.card.id,
                isPhysicalReading = false,
                createdAt = System.currentTimeMillis()
            )
            
            val readingId = readingRepository.saveReading(reading, readingCards)
            
            // Navigate to reading result
            _navigateToReading.value = readingId
        }
    }
    
    private fun calculateEigenvalue(cards: List<Card>): EigenvalueResult {
        var sum = 0
        
        // Sum all card numbers
        for (card in cards) {
            sum += card.number
        }
        
        // Reduce to a number between 1 and 22 (Major Arcana range)
        var reducedValue = sum
        while (reducedValue > 22) {
            reducedValue = reduceNumber(reducedValue)
        }
        
        // Find corresponding Major Arcana card
        val eigenvalueCard = deckRepository.getMajorArcanaCardByNumber(deckId, reducedValue)
        
        return EigenvalueResult(
            value = reducedValue,
            card = eigenvalueCard
        )
    }
    
    private fun reduceNumber(number: Int): Int {
        // Sum the digits
        var sum = 0
        var n = number
        
        while (n > 0) {
            sum += n % 10
            n /= 10
        }
        
        return sum
    }
}

class CardSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private var cardBackBitmap: Bitmap? = null
    private var cards = listOf<Card>()
    private val cardRects = mutableListOf<RectF>()
    private val selectedPositions = mutableSetOf<Int>()
    
    private var onCardSelectedListener: ((Int) -> Unit)? = null
    
    private val cardPaint = Paint()
    private val selectedCardPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    fun setCardBackImage(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    cardBackBitmap = resource
                    invalidate()
                }
                
                override fun onLoadCleared(placeholder: Drawable?) {
                    cardBackBitmap = null
                }
            })
    }
    
    fun setShuffledDeck(cards: List<Card>) {
        this.cards = cards
        selectedPositions.clear()
        calculateCardPositions()
        startShuffleAnimation()
    }
    
    fun setOnCardSelectedListener(listener: (Int) -> Unit) {
        onCardSelectedListener = listener
    }
    
    private fun calculateCardPositions() {
        cardRects.clear()
        
        val cardWidth = width * 0.15f
        val cardHeight = cardWidth * 1.5f
        
        // Arrange cards in a fan pattern
        val centerX = width / 2f
        val centerY = height * 0.6f
        val radius = min(width, height) * 0.4f
        val fanAngleRange = 120f // degrees
        val startAngle = 210f // degrees
        
        val cardCount = min(cards.size, 21) // Limit to 21 visible cards
        
        for (i in 0 until cardCount) {
            val angle = startAngle + (fanAngleRange * i / (cardCount - 1))
            val radians = Math.toRadians(angle.toDouble())
            
            val x = centerX + (radius * cos(radians)).toFloat()
            val y = centerY + (radius * sin(radians)).toFloat()
            
            val rect = RectF(
                x - cardWidth / 2,
                y - cardHeight / 2,
                x + cardWidth / 2,
                y + cardHeight / 2
            )
            
            // Apply rotation to match fan angle
            val matrix = Matrix()
            matrix.setRotate(angle - 90, x, y)
            
            cardRects.add(rect)
        }
    }
    
    private fun startShuffleAnimation() {
        // Animate cards flying in from center
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000
        
        val centerX = width / 2f
        val centerY = height / 2f
        val startRects = cardRects.map { 
            RectF(centerX, centerY, centerX, centerY)
        }
        val endRects = cardRects.toList()
        
        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            
            for (i in cardRects.indices) {
                val start = startRects[i]
                val end = endRects[i]
                
                cardRects[i] = RectF(
                    start.left + (end.left - start.left) * fraction,
                    start.top + (end.top - start.top) * fraction,
                    start.right + (end.right - start.right) * fraction,
                    start.bottom + (end.bottom - start.bottom) * fraction
                )
            }
            
            invalidate()
        }
        
        animator.start()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateCardPositions()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val cardBackBitmap = this.cardBackBitmap ?: return
        
        // Draw cards from back to front (reverse order)
        for (i in cardRects.indices.reversed()) {
            val rect = cardRects[i]
            
            // Draw card back
            canvas.drawBitmap(cardBackBitmap, null, rect, cardPaint)
            
            // Draw selection indicator if selected
            if (i in selectedPositions) {
                canvas.drawRoundRect(rect, 8f, 8f, selectedCardPaint)
            }
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                val x = event.x
                val y = event.y
                
                // Check if a card was tapped
                for (i in cardRects.indices) {
                    if (i !in selectedPositions && cardRects[i].contains(x, y)) {
                        selectedPositions.add(i)
                        onCardSelectedListener?.invoke(i)
                        invalidate()
                        break
                    }
                }
                
                return true
            }
        }
        
        return super.onTouchEvent(event)
    }
}
```

### 4. Reading Result Module

```kotlin
class ReadingResultFragment : Fragment() {
    private lateinit var viewModel: ReadingResultViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val readingId = args.readingId
        
        viewModel = ViewModelProvider(
            this,
            ReadingResultViewModelFactory(readingId)
        ).get(ReadingResultViewModel::class.java)
        
        setupSpreadView()
        observeViewModel()
        setupButtons()
    }
    
    private fun setupSpreadView() {
        binding.spreadView.setOnCardClickListener { card, position ->
            showCardDetailDialog(card, position)
        }
    }
    
    private fun observeViewModel() {
        viewModel.reading.observe(viewLifecycleOwner) { reading ->
            binding.readingTitle.text = reading.title
            binding.interpretationText.text = reading.interpretation
            
            // Set eigenvalue information
            binding.eigenvalueText.text = getString(
                R.string.eigenvalue_format,
                reading.eigenvalue,
                reading.eigenvalueCard.name
            )
            
            // Load eigenvalue card image
            Glide.with(requireContext())
                .load(reading.eigenvalueCard.imageUrl)
                .into(binding.eigenvalueCardImage)
        }
        
        viewModel.readingCards.observe(viewLifecycleOwner) { readingCards ->
            binding.spreadView.setReadingCards(readingCards)
        }
        
        viewModel.spread.observe(viewLifecycleOwner) { spread ->
            binding.spreadName.text = spread.name
        }
    }
    
    private fun setupButtons() {
        binding.addNotesButton.setOnClickListener {
            showAddNotesDialog()
        }
        
        binding.shareButton.setOnClickListener {
            shareReading()
        }
        
        binding.saveButton.setOnClickListener {
            viewModel.saveReading()
            Snackbar.make(
                binding.root,
                R.string.reading_saved,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
    
    private fun showCardDetailDialog(card: Card, position: SpreadPosition) {
        val dialog = CardDetailDialogFragment.newInstance(
            cardId = card.id,
            positionName = position.name,
            isReversed = viewModel.isCardReversed(card.id)
        )
        dialog.show(childFragmentManager, "CardDetailDialog")
    }
    
    private fun showAddNotesDialog() {
        val dialog = AddNotesDialogFragment.newInstance(
            readingId = viewModel.readingId
        )
        dialog.show(childFragmentManager, "AddNotesDialog")
    }
    
    private fun shareReading() {
        viewModel.generateReadingImage { bitmap ->
            if (bitmap != null) {
                // Save bitmap to cache directory
                val file = File(requireContext().cacheDir, "reading_${viewModel.readingId}.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                
                // Create sharing intent
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, viewModel.reading.value?.title)
                    putExtra(Intent.EXTRA_TEXT, viewModel.getShareText())
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                startActivity(Intent.createChooser(intent, getString(R.string.share_reading)))
            }
        }
    }
}

class ReadingResultViewModel(
    val readingId: Long
) : ViewModel() {
    private val readingRepository = ReadingRepository()
    private val spreadRepository = SpreadRepository()
    private val deckRepository = DeckRepository()
    
    private val _reading = MutableLiveData<ReadingWithCards>()
    val reading: LiveData<ReadingWithCards> = _reading
    
    private val _readingCards = MutableLiveData<List<ReadingCard>>()
    val readingCards: LiveData<List<ReadingCard>> = _readingCards
    
    private val _spread = MutableLiveData<Spread>()
    val spread: LiveData<Spread> = _spread
    
    init {
        viewModelScope.launch {
            val readingWithCards = readingRepository.getReadingWithCardsById(readingId)
            _reading.value = readingWithCards
            _readingCards.value = readingWithCards.cards
            
            val spread = spreadRepository.getSpreadById(readingWithCards.reading.spreadId)
            _spread.value = spread
        }
    }
    
    fun isCardReversed(cardId: Long): Boolean {
        return _readingCards.value?.find { it.card.id == cardId }?.isReversed ?: false
    }
    
    fun saveReading() {
        // Reading is already saved, this is just for UI feedback
    }
    
    fun generateReadingImage(callback: (Bitmap?) -> Unit) {
        val reading = _reading.value ?: return callback(null)
        val readingCards = _readingCards.value ?: return callback(null)
        val spread = _spread.value ?: return callback(null)
        
        // Create bitmap with appropriate size
        val bitmap = Bitmap.createBitmap(1200, 1800, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Fill background
        canvas.drawColor(Color.rgb(25, 25, 40))
        
        // Draw title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 60f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(reading.reading.title, 600f, 100f, titlePaint)
        
        // Draw spread name
        val spreadNamePaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(spread.name, 600f, 160f, spreadNamePaint)
        
        // Draw cards
        val cardWidth = 200f
        val cardHeight = 350f
        
        // Calculate positions based on spread layout
        val cardPositions = mutableListOf<RectF>()
        for (position in spread.positions) {
            val x = 600f + (position.xPosition - 0.5f) * 800f
            val y = 600f + (position.yPosition - 0.5f) * 600f
            
            val rect = RectF(
                x - cardWidth / 2,
                y - cardHeight / 2,
                x + cardWidth / 2,
                y + cardHeight / 2
            )
            
            cardPositions.add(rect)
        }
        
        // Draw cards on canvas
        for (i in readingCards.indices) {
            val readingCard = readingCards[i]
            val rect = cardPositions[i]
            
            // Load card image
            val cardBitmap = loadCardBitmap(readingCard.card.imageUrl)
            if (cardBitmap != null) {
                // Apply rotation if card is reversed
                if (readingCard.isReversed) {
                    val matrix = Matrix()
                    matrix.setRotate(180f, cardBitmap.width / 2f, cardBitmap.height / 2f)
                    
                    val rotatedBitmap = Bitmap.createBitmap(
                        cardBitmap,
                        0,
                        0,
                        cardBitmap.width,
                        cardBitmap.height,
                        matrix,
                        true
                    )
                    
                    canvas.drawBitmap(rotatedBitmap, null, rect, null)
                } else {
                    canvas.drawBitmap(cardBitmap, null, rect, null)
                }
            }
            
            // Draw position name
            val positionPaint = Paint().apply {
                color = Color.WHITE
                textSize = 24f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                readingCard.position.name,
                rect.centerX(),
                rect.bottom + 30f,
                positionPaint
            )
        }
        
        // Draw eigenvalue
        val eigenvaluePaint = Paint().apply {
            color = Color.YELLOW
            textSize = 36f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "Eigenvalue: ${reading.reading.eigenvalue} - ${reading.eigenvalueCard.name}",
            600f,
            1100f,
            eigenvaluePaint
        )
        
        // Draw interpretation (truncated)
        val interpretationPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.LEFT
        }
        
        val interpretation = reading.reading.interpretation
        val truncatedInterpretation = if (interpretation.length > 300) {
            interpretation.substring(0, 300) + "..."
        } else {
            interpretation
        }
        
        val textLayout = StaticLayout.Builder.obtain(
            truncatedInterpretation,
            0,
            truncatedInterpretation.length,
            TextPaint(interpretationPaint),
            1000
        ).build()
        
        canvas.save()
        canvas.translate(100f, 1200f)
        textLayout.draw(canvas)
        canvas.restore()
        
        // Draw app name and date at bottom
        val footerPaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "Created with Tarot Reading App • ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())}",
            600f,
            1750f,
            footerPaint
        )
        
        callback(bitmap)
    }
    
    private fun loadCardBitmap(imageUrl: String): Bitmap? {
        try {
            return Glide.with(application)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    fun getShareText(): String {
        val reading = _reading.value ?: return ""
        val spread = _spread.value ?: return ""
        
        return """
            ${reading.reading.title}
            
            Spread: ${spread.name}
            
            ${reading.reading.interpretation.take(300)}...
            
            Eigenvalue: ${reading.reading.eigenvalue} - ${reading.eigenvalueCard.name}
            
            Created with Tarot Reading App
        """.trimIndent()
    }
}

class SpreadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private var readingCards = listOf<ReadingCard>()
    private val cardRects = mutableListOf<RectF>()
    private val cardBitmaps = mutableMapOf<Long, Bitmap>()
    
    private var onCardClickListener: ((Card, SpreadPosition) -> Unit)? = null
    
    fun setReadingCards(readingCards: List<ReadingCard>) {
        this.readingCards = readingCards
        loadCardImages()
        calculateCardPositions()
        invalidate()
    }
    
    fun setOnCardClickListener(listener: (Card, SpreadPosition) -> Unit) {
        onCardClickListener = listener
    }
    
    private fun loadCardImages() {
        cardBitmaps.clear()
        
        for (readingCard in readingCards) {
            Glide.with(this)
                .asBitmap()
                .load(readingCard.card.imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        cardBitmaps[readingCard.card.id] = resource
                        invalidate()
                    }
                    
                    override fun onLoadCleared(placeholder: Drawable?) {
                        cardBitmaps.remove(readingCard.card.id)
                    }
                })
        }
    }
    
    private fun calculateCardPositions() {
        cardRects.clear()
        
        if (readingCards.isEmpty()) return
        
        val cardWidth = width * 0.2f
        val cardHeight = cardWidth * 1.5f
        
        for (readingCard in readingCards) {
            val position = readingCard.position
            
            val x = position.xPosition * width
            val y = position.yPosition * height
            val rotation = position.rotation
            
            val rect = RectF(
                x - cardWidth / 2,
                y - cardHeight / 2,
                x + cardWidth / 2,
                y + cardHeight / 2
            )
            
            cardRects.add(rect)
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateCardPositions()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        for (i in readingCards.indices) {
            val readingCard = readingCards[i]
            val rect = cardRects.getOrNull(i) ?: continue
            
            val bitmap = cardBitmaps[readingCard.card.id]
            if (bitmap != null) {
                // Apply rotation if card is reversed
                if (readingCard.isReversed) {
                    val matrix = Matrix()
                    matrix.setRotate(180f, bitmap.width / 2f, bitmap.height / 2f)
                    
                    val rotatedBitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )
                    
                    canvas.drawBitmap(rotatedBitmap, null, rect, null)
                } else {
                    canvas.drawBitmap(bitmap, null, rect, null)
                }
            } else {
                // Draw placeholder if image not loaded
                val paint = Paint().apply {
                    color = Color.GRAY
                    style = Paint.Style.FILL
                }
                canvas.drawRoundRect(rect, 8f, 8f, paint)
            }
            
            // Draw position name
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 24f
                textAlign = Paint.Align.CENTER
                setShadowLayer(3f, 1f, 1f, Color.BLACK)
            }
            canvas.drawText(
                readingCard.position.name,
                rect.centerX(),
                rect.bottom + 30f,
                textPaint
            )
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                val x = event.x
                val y = event.y
                
                for (i in cardRects.indices) {
                    if (cardRects[i].contains(x, y)) {
                        val readingCard = readingCards.getOrNull(i) ?: continue
                        onCardClickListener?.invoke(readingCard.card, readingCard.position)
                        return true
                    }
                }
            }
        }
        
        return super.onTouchEvent(event)
    }
}
```

## Animation and Visual Effects

### 1. Card Shuffling Animation

```kotlin
class ShuffleAnimator(private val view: View) {
    private val random = Random()
    private val cardPositions = mutableListOf<PointF>()
    private val cardRotations = mutableListOf<Float>()
    private val cardScales = mutableListOf<Float>()
    
    private val animator = ValueAnimator.ofFloat(0f, 1f)
    
    fun shuffle(cardCount: Int, onComplete: () -> Unit) {
        // Initialize starting positions (stacked in center)
        val centerX = view.width / 2f
        val centerY = view.height / 2f
        
        cardPositions.clear()
        cardRotations.clear()
        cardScales.clear()
        
        repeat(cardCount) {
            cardPositions.add(PointF(centerX, centerY))
            cardRotations.add(0f)
            cardScales.add(1f)
        }
        
        // Generate target positions (spread out)
        val targetPositions = generateTargetPositions(cardCount)
        val targetRotations = generateTargetRotations(cardCount)
        
        // Set up animation
        animator.duration = 1500
        animator.interpolator = DecelerateInterpolator()
        
        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            
            for (i in 0 until cardCount) {
                val startPos = PointF(centerX, centerY)
                val targetPos = targetPositions[i]
                
                // Calculate intermediate position with some randomness
                val xOffset = random.nextFloat() * 20 - 10
                val yOffset = random.nextFloat() * 20 - 10
                
                cardPositions[i] = PointF(
                    startPos.x + (targetPos.x - startPos.x) * fraction + xOffset * (1 - fraction),
                    startPos.y + (targetPos.y - startPos.y) * fraction + yOffset * (1 - fraction)
                )
                
                // Calculate intermediate rotation with some wobble
                val wobble = sin(fraction * Math.PI * 4).toFloat() * 5 * (1 - fraction)
                cardRotations[i] = targetRotations[i] * fraction + wobble
                
                // Scale cards
                val scaleFactor = 0.8f + 0.2f * fraction
                cardScales[i] = scaleFactor
            }
            
            view.invalidate()
        }
        
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete()
            }
        })
        
        animator.start()
    }
    
    private fun generateTargetPositions(cardCount: Int): List<PointF> {
        val positions = mutableListOf<PointF>()
        
        // Arrange in a fan pattern
        val centerX = view.width / 2f
        val centerY = view.height * 0.6f
        val radius = min(view.width, view.height) * 0.4f
        val fanAngleRange = 120f // degrees
        val startAngle = 210f // degrees
        
        for (i in 0 until cardCount) {
            val angle = startAngle + (fanAngleRange * i / (cardCount - 1))
            val radians = Math.toRadians(angle.toDouble())
            
            val x = centerX + (radius * cos(radians)).toFloat()
            val y = centerY + (radius * sin(radians)).toFloat()
            
            positions.add(PointF(x, y))
        }
        
        return positions
    }
    
    private fun generateTargetRotations(cardCount: Int): List<Float> {
        val rotations = mutableListOf<Float>()
        
        val fanAngleRange = 120f // degrees
        val startAngle = 210f // degrees
        
        for (i in 0 until cardCount) {
            val angle = startAngle + (fanAngleRange * i / (cardCount - 1))
            rotations.add(angle - 90)
        }
        
        return rotations
    }
    
    fun getCardPositions(): List<PointF> = cardPositions
    fun getCardRotations(): List<Float> = cardRotations
    fun getCardScales(): List<Float> = cardScales
}
```

### 2. Card Reveal Animation

```kotlin
class CardRevealAnimator(private val view: View) {
    private val animator = ValueAnimator.ofFloat(0f, 1f)
    private var currentCard: Int = -1
    private var revealProgress: Float = 0f
    
    fun revealCard(cardIndex: Int, onComplete: () -> Unit) {
        currentCard = cardIndex
        revealProgress = 0f
        
        animator.duration = 800
        animator.interpolator = AccelerateDecelerateInterpolator()
        
        animator.addUpdateListener { animation ->
            revealProgress = animation.animatedValue as Float
            view.invalidate()
        }
        
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete()
            }
        })
        
        animator.start()
    }
    
    fun drawCardReveal(canvas: Canvas, cardRect: RectF, cardBackBitmap: Bitmap?, cardFrontBitmap: Bitmap?) {
        if (currentCard < 0) return
        
        // First half of animation: card flips to edge
        if (revealProgress < 0.5f) {
            val scale = 1 - (revealProgress * 2) * 0.1f
            val width = cardRect.width() * scale
            
            val scaledRect = RectF(
                cardRect.centerX() - width / 2,
                cardRect.top,
                cardRect.centerX() + width / 2,
                cardRect.bottom
            )
            
            cardBackBitmap?.let {
                canvas.drawBitmap(it, null, scaledRect, null)
            }
        } 
        // Second half of animation: card flips from edge revealing front
        else {
            val progress = (revealProgress - 0.5f) * 2
            val scale = 0.9f + progress * 0.1f
            val width = cardRect.width() * scale
            
            val scaledRect = RectF(
                cardRect.centerX() - width / 2,
                cardRect.top,
                cardRect.centerX() + width / 2,
                cardRect.bottom
            )
            
            cardFrontBitmap?.let {
                canvas.drawBitmap(it, null, scaledRect, null)
            }
        }
    }
    
    fun isRevealing(): Boolean = animator.isRunning
    fun getCurrentCard(): Int = currentCard
    fun getRevealProgress(): Float = revealProgress
}
```

### 3. Spread Layout Animation

```kotlin
class SpreadLayoutAnimator(private val view: View) {
    private val animator = ValueAnimator.ofFloat(0f, 1f)
    private val cardPositions = mutableListOf<PointF>()
    private val cardRotations = mutableListOf<Float>()
    private val cardScales = mutableListOf<Float>()
    
    fun animateToSpread(
        cards: List<ReadingCard>,
        spread: Spread,
        onComplete: () -> Unit
    ) {
        // Initialize starting positions (cards in hand)
        val startX = view.width * 0.5f
        val startY = view.height * 1.2f // Below screen
        
        cardPositions.clear()
        cardRotations.clear()
        cardScales.clear()
        
        repeat(cards.size) {
            cardPositions.add(PointF(startX, startY))
            cardRotations.add(0f)
            cardScales.add(0.8f)
        }
        
        // Generate target positions based on spread
        val targetPositions = mutableListOf<PointF>()
        val targetRotations = mutableListOf<Float>()
        
        for (position in spread.positions) {
            val x = position.xPosition * view.width
            val y = position.yPosition * view.height
            targetPositions.add(PointF(x, y))
            targetRotations.add(position.rotation)
        }
        
        // Set up animation
        animator.duration = 1500
        animator.interpolator = OvershootInterpolator(0.8f)
        
        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            
            for (i in cards.indices) {
                // Stagger the animations
                val delay = i * 0.1f
                val adjustedFraction = (fraction - delay) / (1 - delay)
                val cardFraction = max(0f, min(1f, adjustedFraction))
                
                val startPos = PointF(startX, startY)
                val targetPos = targetPositions.getOrNull(i) ?: continue
                
                cardPositions[i] = PointF(
                    startPos.x + (targetPos.x - startPos.x) * cardFraction,
                    startPos.y + (targetPos.y - startPos.y) * cardFraction
                )
                
                cardRotations[i] = targetRotations.getOrNull(i) ?: 0f
                cardScales[i] = 0.8f + 0.2f * cardFraction
            }
            
            view.invalidate()
        }
        
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete()
            }
        })
        
        animator.start()
    }
    
    fun getCardPositions(): List<PointF> = cardPositions
    fun getCardRotations(): List<Float> = cardRotations
    fun getCardScales(): List<Float> = cardScales
}
```

## Interpretation Generation

### 1. Card Interpretation Engine

```kotlin
class InterpretationEngine(
    private val context: Context
) {
    private val cardRepository = CardRepository(context)
    private val spreadRepository = SpreadRepository(context)
    
    fun generateCardInterpretation(
        card: Card,
        position: SpreadPosition,
        isReversed: Boolean
    ): String {
        // Get card meaning based on orientation
        val meaning = if (isReversed) card.reversedMeaning else card.uprightMeaning
        
        // Get keywords
        val keywords = if (isReversed) {
            card.keywords.filter { it.isReversed }
        } else {
            card.keywords.filter { !it.isReversed }
        }
        
        // Get position meaning
        val positionMeaning = position.description
        
        // Generate interpretation template
        val template = if (isReversed) {
            context.getString(R.string.reversed_card_in_position_template)
        } else {
            context.getString(R.string.upright_card_in_position_template)
        }
        
        // Format keywords as comma-separated list
        val keywordsList = keywords.joinToString(", ") { it.keyword }
        
        // Replace placeholders in template
        return template
            .replace("{CARD_NAME}", card.name)
            .replace("{POSITION_NAME}", position.name)
            .replace("{POSITION_MEANING}", positionMeaning)
            .replace("{CARD_MEANING}", meaning)
            .replace("{KEYWORDS}", keywordsList)
    }
    
    fun generateHolisticReading(
        spread: Spread,
        readingCards: List<ReadingCard>
    ): String {
        // Get spread introduction
        val spreadIntro = context.getString(
            R.string.spread_introduction_template,
            spread.name,
            spread.description
        )
        
        // Generate individual card interpretations
        val cardInterpretations = readingCards.map { readingCard ->
            val card = readingCard.card
            val position = readingCard.position
            val isReversed = readingCard.isReversed
            
            generateCardInterpretation(card, position, isReversed)
        }
        
        // Analyze card relationships
        val relationships = analyzeCardRelationships(readingCards)
        
        // Generate conclusion
        val conclusion = generateConclusion(readingCards)
        
        // Combine all parts
        return buildString {
            append(spreadIntro)
            append("\n\n")
            
            cardInterpretations.forEachIndexed { index, interpretation ->
                append("${index + 1}. ")
                append(interpretation)
                append("\n\n")
            }
            
            if (relationships.isNotEmpty()) {
                append("Card Relationships:\n")
                append(relationships)
                append("\n\n")
            }
            
            append("Summary:\n")
            append(conclusion)
        }
    }
    
    private fun analyzeCardRelationships(readingCards: List<ReadingCard>): String {
        // Skip for single card readings
        if (readingCards.size <= 1) {
            return ""
        }
        
        val relationships = mutableListOf<String>()
        
        // Look for patterns
        val suits = readingCards.groupBy { it.card.suit }
        val numbers = readingCards.groupBy { it.card.number }
        val arcanaTypes = readingCards.groupBy { it.card.arcanaType }
        val reversals = readingCards.count { it.isReversed }
        
        // Check for suit dominance
        suits.entries.filter { it.value.size > 1 }.maxByOrNull { it.value.size }?.let { (suit, cards) ->
            if (cards.size >= readingCards.size / 3) {
                relationships.add(
                    context.getString(
                        R.string.suit_dominance_template,
                        suit,
                        cards.size,
                        getSuitMeaning(suit)
                    )
                )
            }
        }
        
        // Check for number patterns
        numbers.entries.filter { it.value.size > 1 }.forEach { (number, cards) ->
            relationships.add(
                context.getString(
                    R.string.number_pattern_template,
                    number,
                    cards.size,
                    getNumberMeaning(number)
                )
            )
        }
        
        // Check arcana balance
        val majorCount = arcanaTypes["Major"]?.size ?: 0
        val minorCount = readingCards.size - majorCount
        
        if (majorCount > minorCount) {
            relationships.add(context.getString(R.string.major_arcana_dominance))
        } else if (minorCount > majorCount * 2) {
            relationships.add(context.getString(R.string.minor_arcana_dominance))
        }
        
        // Check reversal ratio
        val reversalRatio = reversals.toFloat() / readingCards.size
        when {
            reversalRatio > 0.7 -> relationships.add(context.getString(R.string.many_reversals))
            reversalRatio < 0.3 -> relationships.add(context.getString(R.string.few_reversals))
        }
        
        return relationships.joinToString("\n\n")
    }
    
    private fun getSuitMeaning(suit: String): String {
        return when (suit) {
            "Wands" -> context.getString(R.string.wands_meaning)
            "Cups" -> context.getString(R.string.cups_meaning)
            "Swords" -> context.getString(R.string.swords_meaning)
            "Pentacles" -> context.getString(R.string.pentacles_meaning)
            else -> ""
        }
    }
    
    private fun getNumberMeaning(number: Int): String {
        return when (number) {
            1 -> context.getString(R.string.number_1_meaning)
            2 -> context.getString(R.string.number_2_meaning)
            3 -> context.getString(R.string.number_3_meaning)
            4 -> context.getString(R.string.number_4_meaning)
            5 -> context.getString(R.string.number_5_meaning)
            6 -> context.getString(R.string.number_6_meaning)
            7 -> context.getString(R.string.number_7_meaning)
            8 -> context.getString(R.string.number_8_meaning)
            9 -> context.getString(R.string.number_9_meaning)
            10 -> context.getString(R.string.number_10_meaning)
            else -> ""
        }
    }
    
    private fun generateConclusion(readingCards: List<ReadingCard>): String {
        // Extract key themes
        val themes = mutableSetOf<String>()
        
        for (readingCard in readingCards) {
            val card = readingCard.card
            val keywords = if (readingCard.isReversed) {
                card.keywords.filter { it.isReversed }
            } else {
                card.keywords.filter { !it.isReversed }
            }
            
            themes.addAll(keywords.map { it.keyword })
        }
        
        // Count elements
        val elements = readingCards.groupBy { it.card.element }
        val dominantElement = elements.entries.maxByOrNull { it.value.size }?.key
        
        // Generate conclusion template
        val template = context.getString(R.string.conclusion_template)
        
        // Get key themes (limit to top 5)
        val keyThemes = themes.take(5).joinToString(", ")
        
        // Replace placeholders
        return template
            .replace("{KEY_THEMES}", keyThemes)
            .replace("{DOMINANT_ELEMENT}", dominantElement ?: "")
    }
    
    fun calculateEigenvalue(cards: List<Card>): EigenvalueResult {
        var sum = 0
        
        // Sum all card numbers
        for (card in cards) {
            sum += card.number
        }
        
        // Reduce to a number between 1 and 22 (Major Arcana range)
        var reducedValue = sum
        while (reducedValue > 22) {
            reducedValue = reduceNumber(reducedValue)
        }
        
        // Find corresponding Major Arcana card
        val eigenvalueCard = cardRepository.getMajorArcanaCardByNumber(reducedValue)
        
        return EigenvalueResult(
            value = reducedValue,
            card = eigenvalueCard,
            meaning = getEigenvalueMeaning(eigenvalueCard)
        )
    }
    
    private fun reduceNumber(number: Int): Int {
        // Sum the digits
        var sum = 0
        var n = number
        
        while (n > 0) {
            sum += n % 10
            n /= 10
        }
        
        return sum
    }
    
    private fun getEigenvalueMeaning(card: Card): String {
        return context.getString(
            R.string.eigenvalue_meaning_template,
            card.name,
            card.uprightMeaning
        )
    }
}

data class EigenvalueResult(
    val value: Int,
    val card: Card,
    val meaning: String
)
```

## User Experience Enhancements

### 1. Haptic Feedback

```kotlin
class TarotHaptics(private val context: Context) {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    
    fun cardSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(30)
        }
    }
    
    fun cardReveal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 60, 50, 30)
            val amplitudes = intArrayOf(0, 120, 80, 40)
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
    
    fun spreadComplete() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 100, 50, 100, 50, 150)
            val amplitudes = intArrayOf(0, 80, 0, 120, 0, 200)
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(300)
        }
    }
}
```

### 2. Sound Effects

```kotlin
class TarotSoundEffects(private val context: Context) {
    private val soundPool: SoundPool
    
    private var cardShuffleSound: Int = 0
    private var cardSelectSound: Int = 0
    private var cardRevealSound: Int = 0
    private var spreadCompleteSound: Int = 0
    
    init {
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            @Suppress("DEPRECATION")
            SoundPool(4, AudioManager.STREAM_MUSIC, 0)
        }
        
        // Load sounds
        cardShuffleSound = soundPool.load(context, R.raw.card_shuffle, 1)
        cardSelectSound = soundPool.load(context, R.raw.card_select, 1)
        cardRevealSound = soundPool.load(context, R.raw.card_reveal, 1)
        spreadCompleteSound = soundPool.load(context, R.raw.spread_complete, 1)
    }
    
    fun playCardShuffle() {
        soundPool.play(cardShuffleSound, 0.7f, 0.7f, 1, 0, 1f)
    }
    
    fun playCardSelect() {
        soundPool.play(cardSelectSound, 1f, 1f, 1, 0, 1f)
    }
    
    fun playCardReveal() {
        soundPool.play(cardRevealSound, 0.9f, 0.9f, 1, 0, 1f)
    }
    
    fun playSpreadComplete() {
        soundPool.play(spreadCompleteSound, 1f, 1f, 1, 0, 1.2f)
    }
    
    fun release() {
        soundPool.release()
    }
}
```

### 3. Ambient Background

```kotlin
class TarotAmbientBackground(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentTheme: AmbientTheme = AmbientTheme.MYSTICAL
    
    enum class AmbientTheme {
        MYSTICAL,
        PEACEFUL,
        INTENSE,
        SILENT
    }
    
    fun startAmbient(theme: AmbientTheme = currentTheme) {
        stopAmbient()
        
        if (theme == AmbientTheme.SILENT) {
            return
        }
        
        currentTheme = theme
        
        val resId = when (theme) {
            AmbientTheme.MYSTICAL -> R.raw.ambient_mystical
            AmbientTheme.PEACEFUL -> R.raw.ambient_peaceful
            AmbientTheme.INTENSE -> R.raw.ambient_intense
            AmbientTheme.SILENT -> return
        }
        
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true
            setVolume(0.5f, 0.5f)
            start()
        }
    }
    
    fun stopAmbient() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
    
    fun setVolume(volume: Float) {
        val v = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(v, v)
    }
    
    fun changeTheme(theme: AmbientTheme) {
        if (theme != currentTheme) {
            startAmbient(theme)
        }
    }
}
```