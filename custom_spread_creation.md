# Custom Spread Creation Implementation

## Overview

The Custom Spread Creation feature allows users to design their own unique Tarot spreads with custom positions and meanings. This document outlines the implementation details for creating an intuitive and flexible spread design experience.

## User Flow

1. **Spread Design Initialization**
   - User accesses the custom spread creation feature
   - User names their new spread and provides a description

2. **Canvas Interaction**
   - User interacts with a design canvas to place card positions
   - User can drag, rotate, and arrange positions freely

3. **Position Configuration**
   - User defines names and meanings for each position
   - User can edit or delete existing positions

4. **Spread Management**
   - User saves the custom spread for future use
   - User can edit or delete previously created spreads

## Implementation Details

### 1. Custom Spread Creation Fragment

```kotlin
class CustomSpreadCreationFragment : Fragment() {
    private lateinit var viewModel: CustomSpreadViewModel
    private lateinit var binding: FragmentCustomSpreadCreationBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(CustomSpreadViewModel::class.java)
        
        setupDesignCanvas()
        setupPositionsList()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupDesignCanvas() {
        binding.designCanvas.setOnPositionAddedListener { x, y ->
            viewModel.addPosition(x, y)
        }
        
        binding.designCanvas.setOnPositionMovedListener { position, x, y ->
            viewModel.updatePositionCoordinates(position.id, x, y)
        }
        
        binding.designCanvas.setOnPositionRotatedListener { position, rotation ->
            viewModel.updatePositionRotation(position.id, rotation)
        }
        
        binding.designCanvas.setOnPositionSelectedListener { position ->
            viewModel.selectPosition(position)
        }
    }
    
    private fun setupPositionsList() {
        val adapter = SpreadPositionAdapter(
            onEditClick = { position ->
                showEditPositionDialog(position)
            },
            onDeleteClick = { position ->
                viewModel.deletePosition(position.id)
            }
        )
        
        binding.positionsList.adapter = adapter
        binding.positionsList.layoutManager = LinearLayoutManager(requireContext())
        
        viewModel.positions.observe(viewLifecycleOwner) { positions ->
            adapter.submitList(positions)
            binding.designCanvas.setPositions(positions)
            
            // Update UI based on position count
            binding.emptyStateGroup.isVisible = positions.isEmpty()
            binding.saveButton.isEnabled = positions.isNotEmpty()
        }
    }
    
    private fun setupButtons() {
        binding.addPositionButton.setOnClickListener {
            binding.designCanvas.enterAddMode()
            Snackbar.make(
                binding.root,
                R.string.tap_canvas_to_add_position,
                Snackbar.LENGTH_SHORT
            ).show()
        }
        
        binding.saveButton.setOnClickListener {
            if (validateSpreadData()) {
                viewModel.saveSpread(
                    name = binding.spreadNameInput.text.toString(),
                    description = binding.spreadDescriptionInput.text.toString()
                )
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.selectedPosition.observe(viewLifecycleOwner) { position ->
            binding.designCanvas.setSelectedPosition(position)
        }
        
        viewModel.saveComplete.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(
                    binding.root,
                    R.string.spread_saved_successfully,
                    Snackbar.LENGTH_SHORT
                ).show()
                
                findNavController().navigateUp()
            }
        }
    }
    
    private fun showEditPositionDialog(position: SpreadPosition) {
        val dialog = EditPositionDialogFragment.newInstance(position)
        dialog.setOnPositionEditedListener { editedPosition ->
            viewModel.updatePosition(editedPosition)
        }
        dialog.show(childFragmentManager, "EditPositionDialog")
    }
    
    private fun validateSpreadData(): Boolean {
        var isValid = true
        
        // Validate spread name
        if (binding.spreadNameInput.text.isNullOrBlank()) {
            binding.spreadNameInput.error = getString(R.string.spread_name_required)
            isValid = false
        }
        
        // Validate position count
        if (viewModel.positions.value.isNullOrEmpty()) {
            Snackbar.make(
                binding.root,
                R.string.at_least_one_position_required,
                Snackbar.LENGTH_SHORT
            ).show()
            isValid = false
        }
        
        // Validate that all positions have names
        val unnamedPositions = viewModel.positions.value?.filter { it.name.isBlank() }
        if (!unnamedPositions.isNullOrEmpty()) {
            Snackbar.make(
                binding.root,
                R.string.all_positions_must_have_names,
                Snackbar.LENGTH_SHORT
            ).show()
            isValid = false
        }
        
        return isValid
    }
}
```

### 2. Custom Spread ViewModel

```kotlin
class CustomSpreadViewModel : ViewModel() {
    private val spreadRepository = SpreadRepository()
    
    private val _positions = MutableLiveData<List<SpreadPosition>>(emptyList())
    val positions: LiveData<List<SpreadPosition>> = _positions
    
    private val _selectedPosition = MutableLiveData<SpreadPosition?>()
    val selectedPosition: LiveData<SpreadPosition?> = _selectedPosition
    
    private val _saveComplete = MutableLiveData<Boolean>()
    val saveComplete: LiveData<Boolean> = _saveComplete
    
    private var nextPositionId = 1
    
    fun addPosition(x: Float, y: Float) {
        val newPosition = SpreadPosition(
            id = nextPositionId++,
            name = "Position ${_positions.value?.size?.plus(1) ?: 1}",
            description = "",
            xPosition = x,
            yPosition = y,
            rotation = 0f
        )
        
        val currentPositions = _positions.value.orEmpty().toMutableList()
        currentPositions.add(newPosition)
        _positions.value = currentPositions
        
        // Select the new position
        _selectedPosition.value = newPosition
    }
    
    fun updatePosition(position: SpreadPosition) {
        val currentPositions = _positions.value.orEmpty().toMutableList()
        val index = currentPositions.indexOfFirst { it.id == position.id }
        
        if (index != -1) {
            currentPositions[index] = position
            _positions.value = currentPositions
            
            // Update selected position if it's the one being edited
            if (_selectedPosition.value?.id == position.id) {
                _selectedPosition.value = position
            }
        }
    }
    
    fun updatePositionCoordinates(positionId: Int, x: Float, y: Float) {
        val currentPositions = _positions.value.orEmpty().toMutableList()
        val index = currentPositions.indexOfFirst { it.id == positionId }
        
        if (index != -1) {
            val position = currentPositions[index]
            val updatedPosition = position.copy(xPosition = x, yPosition = y)
            currentPositions[index] = updatedPosition
            _positions.value = currentPositions
            
            // Update selected position if it's the one being moved
            if (_selectedPosition.value?.id == positionId) {
                _selectedPosition.value = updatedPosition
            }
        }
    }
    
    fun updatePositionRotation(positionId: Int, rotation: Float) {
        val currentPositions = _positions.value.orEmpty().toMutableList()
        val index = currentPositions.indexOfFirst { it.id == positionId }
        
        if (index != -1) {
            val position = currentPositions[index]
            val updatedPosition = position.copy(rotation = rotation)
            currentPositions[index] = updatedPosition
            _positions.value = currentPositions
            
            // Update selected position if it's the one being rotated
            if (_selectedPosition.value?.id == positionId) {
                _selectedPosition.value = updatedPosition
            }
        }
    }
    
    fun deletePosition(positionId: Int) {
        val currentPositions = _positions.value.orEmpty().toMutableList()
        val index = currentPositions.indexOfFirst { it.id == positionId }
        
        if (index != -1) {
            currentPositions.removeAt(index)
            _positions.value = currentPositions
            
            // Clear selected position if it's the one being deleted
            if (_selectedPosition.value?.id == positionId) {
                _selectedPosition.value = null
            }
        }
    }
    
    fun selectPosition(position: SpreadPosition?) {
        _selectedPosition.value = position
    }
    
    fun saveSpread(name: String, description: String) {
        viewModelScope.launch {
            val positions = _positions.value.orEmpty()
            
            if (positions.isEmpty() || name.isBlank()) {
                _saveComplete.value = false
                return@launch
            }
            
            // Create spread entity
            val spread = Spread(
                id = 0, // Room will generate ID
                name = name,
                description = description,
                isCustom = true,
                isDefault = false,
                isPremium = false,
                userId = userRepository.getCurrentUserId(),
                createdAt = System.currentTimeMillis()
            )
            
            // Normalize position IDs for database
            val normalizedPositions = positions.mapIndexed { index, position ->
                position.copy(
                    id = index + 1,
                    positionNumber = index + 1
                )
            }
            
            // Save to database
            val success = spreadRepository.saveCustomSpread(spread, normalizedPositions)
            _saveComplete.value = success
        }
    }
}
```

### 3. Design Canvas View

```kotlin
class SpreadDesignCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val positions = mutableListOf<SpreadPosition>()
    private var selectedPosition: SpreadPosition? = null
    private var isInAddMode = false
    
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    
    private var isRotating = false
    private var rotationStartAngle = 0f
    
    private val cardWidth = dpToPx(60f)
    private val cardHeight = dpToPx(90f)
    
    private val cardPaint = Paint().apply {
        color = Color.GRAY
        alpha = 100
        style = Paint.Style.FILL
    }
    
    private val selectedCardPaint = Paint().apply {
        color = Color.BLUE
        alpha = 100
        style = Paint.Style.FILL
    }
    
    private val cardOutlinePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = dpToPx(14f)
        textAlign = Paint.Align.CENTER
        setShadowLayer(3f, 1f, 1f, Color.BLACK)
    }
    
    private val rotationHandlePaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.FILL
    }
    
    private var onPositionAddedListener: ((Float, Float) -> Unit)? = null
    private var onPositionMovedListener: ((SpreadPosition, Float, Float) -> Unit)? = null
    private var onPositionRotatedListener: ((SpreadPosition, Float) -> Unit)? = null
    private var onPositionSelectedListener: ((SpreadPosition) -> Unit)? = null
    
    fun setOnPositionAddedListener(listener: (Float, Float) -> Unit) {
        onPositionAddedListener = listener
    }
    
    fun setOnPositionMovedListener(listener: (SpreadPosition, Float, Float) -> Unit) {
        onPositionMovedListener = listener
    }
    
    fun setOnPositionRotatedListener(listener: (SpreadPosition, Float) -> Unit) {
        onPositionRotatedListener = listener
    }
    
    fun setOnPositionSelectedListener(listener: (SpreadPosition) -> Unit) {
        onPositionSelectedListener = listener
    }
    
    fun setPositions(positions: List<SpreadPosition>) {
        this.positions.clear()
        this.positions.addAll(positions)
        invalidate()
    }
    
    fun setSelectedPosition(position: SpreadPosition?) {
        selectedPosition = position
        invalidate()
    }
    
    fun enterAddMode() {
        isInAddMode = true
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw grid lines for visual reference
        drawGrid(canvas)
        
        // Draw all positions
        for (position in positions) {
            val isSelected = position.id == selectedPosition?.id
            drawPosition(canvas, position, isSelected)
        }
    }
    
    private fun drawGrid(canvas: Canvas) {
        val gridPaint = Paint().apply {
            color = Color.GRAY
            alpha = 50
            strokeWidth = 1f
        }
        
        // Draw vertical lines
        val verticalSpacing = width / 10f
        for (i in 1 until 10) {
            val x = i * verticalSpacing
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
        }
        
        // Draw horizontal lines
        val horizontalSpacing = height / 10f
        for (i in 1 until 10) {
            val y = i * horizontalSpacing
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
        }
    }
    
    private fun drawPosition(canvas: Canvas, position: SpreadPosition, isSelected: Boolean) {
        val x = position.xPosition * width
        val y = position.yPosition * height
        
        // Save canvas state before rotation
        canvas.save()
        
        // Rotate canvas around position center
        canvas.rotate(position.rotation, x, y)
        
        // Calculate card rectangle
        val rect = RectF(
            x - cardWidth / 2,
            y - cardHeight / 2,
            x + cardWidth / 2,
            y + cardHeight / 2
        )
        
        // Draw card
        canvas.drawRoundRect(rect, 8f, 8f, if (isSelected) selectedCardPaint else cardPaint)
        canvas.drawRoundRect(rect, 8f, 8f, cardOutlinePaint)
        
        // Draw position number
        canvas.drawText(
            position.name.take(10),
            rect.centerX(),
            rect.centerY(),
            textPaint
        )
        
        // Draw rotation handle if selected
        if (isSelected) {
            val handleRadius = dpToPx(10f)
            canvas.drawCircle(rect.centerX(), rect.top - handleRadius * 1.5f, handleRadius, rotationHandlePaint)
        }
        
        // Restore canvas to original state
        canvas.restore()
    }
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Get the pointer ID
                activePointerId = event.getPointerId(0)
                lastTouchX = x
                lastTouchY = y
                
                if (isInAddMode) {
                    // Add new position
                    onPositionAddedListener?.invoke(x / width, y / height)
                    isInAddMode = false
                } else {
                    // Check if rotation handle is touched
                    val selectedPos = selectedPosition
                    if (selectedPos != null) {
                        val posX = selectedPos.xPosition * width
                        val posY = selectedPos.yPosition * height
                        
                        // Calculate rotation handle position
                        val handleX = posX
                        val handleY = posY - cardHeight / 2 - dpToPx(15f)
                        
                        // Check if touch is on rotation handle
                        val handleRadius = dpToPx(15f)
                        if (sqrt((x - handleX).pow(2) + (y - handleY).pow(2)) <= handleRadius) {
                            isRotating = true
                            rotationStartAngle = calculateAngle(posX, posY, x, y)
                            return true
                        }
                    }
                    
                    // Check if a position is touched
                    val touchedPosition = getTouchedPosition(x, y)
                    if (touchedPosition != null) {
                        selectedPosition = touchedPosition
                        onPositionSelectedListener?.invoke(touchedPosition)
                        invalidate()
                    } else {
                        // Deselect if tapping empty space
                        selectedPosition = null
                        onPositionSelectedListener?.invoke(null)
                        invalidate()
                    }
                }
                return true
            }
            
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerId)
                if (pointerIndex < 0) return false
                
                val currentX = event.getX(pointerIndex)
                val currentY = event.getY(pointerIndex)
                
                val selectedPos = selectedPosition
                if (selectedPos != null) {
                    if (isRotating) {
                        // Handle rotation
                        val posX = selectedPos.xPosition * width
                        val posY = selectedPos.yPosition * height
                        
                        val currentAngle = calculateAngle(posX, posY, currentX, currentY)
                        val angleDelta = currentAngle - rotationStartAngle
                        
                        // Update rotation
                        val newRotation = (selectedPos.rotation + angleDelta) % 360
                        onPositionRotatedListener?.invoke(selectedPos, newRotation)
                        
                        rotationStartAngle = currentAngle
                    } else {
                        // Handle dragging
                        val dx = currentX - lastTouchX
                        val dy = currentY - lastTouchY
                        
                        val newX = (selectedPos.xPosition * width + dx) / width
                        val newY = (selectedPos.yPosition * height + dy) / height
                        
                        // Clamp values to [0,1]
                        val clampedX = newX.coerceIn(0f, 1f)
                        val clampedY = newY.coerceIn(0f, 1f)
                        
                        onPositionMovedListener?.invoke(selectedPos, clampedX, clampedY)
                    }
                }
                
                lastTouchX = currentX
                lastTouchY = currentY
                return true
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
                isRotating = false
                return true
            }
            
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                
                if (pointerId == activePointerId) {
                    // Choose a new pointer to track
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = event.getX(newPointerIndex)
                    lastTouchY = event.getY(newPointerIndex)
                    activePointerId = event.getPointerId(newPointerIndex)
                }
                return true
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    private fun getTouchedPosition(x: Float, y: Float): SpreadPosition? {
        // Check in reverse order to handle overlapping positions (last added on top)
        for (i in positions.indices.reversed()) {
            val position = positions[i]
            val posX = position.xPosition * width
            val posY = position.yPosition * height
            
            // Create a matrix for the position's rotation
            val matrix = Matrix()
            matrix.setRotate(position.rotation, posX, posY)
            
            // Create inverse matrix to transform touch point to card's coordinate space
            val inverse = Matrix()
            matrix.invert(inverse)
            
            // Transform touch point
            val points = floatArrayOf(x, y)
            inverse.mapPoints(points)
            val transformedX = points[0]
            val transformedY = points[1]
            
            // Check if transformed point is inside card rectangle
            val rect = RectF(
                posX - cardWidth / 2,
                posY - cardHeight / 2,
                posX + cardWidth / 2,
                posY + cardHeight / 2
            )
            
            if (rect.contains(transformedX, transformedY)) {
                return position
            }
        }
        
        return null
    }
    
    private fun calculateAngle(centerX: Float, centerY: Float, touchX: Float, touchY: Float): Float {
        val dx = touchX - centerX
        val dy = touchY - centerY
        
        // Calculate angle in degrees
        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        
        // Normalize to [0, 360)
        if (angle < 0) {
            angle += 360f
        }
        
        return angle
    }
    
    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}
```

### 4. Position Edit Dialog

```kotlin
class EditPositionDialogFragment : DialogFragment() {
    private lateinit var binding: DialogEditPositionBinding
    private lateinit var position: SpreadPosition
    
    private var onPositionEditedListener: ((SpreadPosition) -> Unit)? = null
    
    companion object {
        private const val ARG_POSITION = "position"
        
        fun newInstance(position: SpreadPosition): EditPositionDialogFragment {
            val fragment = EditPositionDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEditPositionBinding.inflate(layoutInflater)
        
        position = arguments?.getParcelable(ARG_POSITION)
            ?: throw IllegalArgumentException("Position must be provided")
        
        // Set up the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_position)
            .setView(binding.root)
            .setPositiveButton(R.string.save, null) // Set listener later to prevent auto-dismiss
            .setNegativeButton(R.string.cancel, null)
            .create()
        
        // Initialize fields with position data
        binding.positionNameInput.setText(position.name)
        binding.positionDescriptionInput.setText(position.description)
        
        return dialog
    }
    
    override fun onStart() {
        super.onStart()
        
        // Override positive button to validate before dismissing
        (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            if (validateInputs()) {
                val updatedPosition = position.copy(
                    name = binding.positionNameInput.text.toString(),
                    description = binding.positionDescriptionInput.text.toString()
                )
                
                onPositionEditedListener?.invoke(updatedPosition)
                dismiss()
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Validate position name
        if (binding.positionNameInput.text.isNullOrBlank()) {
            binding.positionNameInput.error = getString(R.string.position_name_required)
            isValid = false
        }
        
        return isValid
    }
    
    fun setOnPositionEditedListener(listener: (SpreadPosition) -> Unit) {
        onPositionEditedListener = listener
    }
}
```

### 5. Position Adapter

```kotlin
class SpreadPositionAdapter(
    private val onEditClick: (SpreadPosition) -> Unit,
    private val onDeleteClick: (SpreadPosition) -> Unit
) : ListAdapter<SpreadPosition, SpreadPositionViewHolder>(SpreadPositionDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpreadPositionViewHolder {
        val binding = ItemSpreadPositionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpreadPositionViewHolder(binding, onEditClick, onDeleteClick)
    }
    
    override fun onBindViewHolder(holder: SpreadPositionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SpreadPositionViewHolder(
    private val binding: ItemSpreadPositionBinding,
    private val onEditClick: (SpreadPosition) -> Unit,
    private val onDeleteClick: (SpreadPosition) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(position: SpreadPosition) {
        binding.positionName.text = position.name
        binding.positionDescription.text = position.description.ifEmpty {
            binding.root.context.getString(R.string.no_description)
        }
        
        binding.editButton.setOnClickListener {
            onEditClick(position)
        }
        
        binding.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
    }
}

class SpreadPositionDiffCallback : DiffUtil.ItemCallback<SpreadPosition>() {
    override fun areItemsTheSame(oldItem: SpreadPosition, newItem: SpreadPosition): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: SpreadPosition, newItem: SpreadPosition): Boolean {
        return oldItem == newItem
    }
}
```

### 6. Custom Spread Management

```kotlin
class CustomSpreadManagementFragment : Fragment() {
    private lateinit var viewModel: CustomSpreadManagementViewModel
    private lateinit var binding: FragmentCustomSpreadManagementBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(CustomSpreadManagementViewModel::class.java)
        
        setupSpreadsList()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupSpreadsList() {
        val adapter = CustomSpreadAdapter(
            onSpreadClick = { spread ->
                findNavController().navigate(
                    CustomSpreadManagementFragmentDirections.actionToSpreadDetail(spread.id)
                )
            },
            onEditClick = { spread ->
                findNavController().navigate(
                    CustomSpreadManagementFragmentDirections.actionToEditSpread(spread.id)
                )
            },
            onDeleteClick = { spread ->
                showDeleteConfirmationDialog(spread)
            }
        )
        
        binding.spreadsList.adapter = adapter
        binding.spreadsList.layoutManager = LinearLayoutManager(requireContext())
        
        viewModel.customSpreads.observe(viewLifecycleOwner) { spreads ->
            adapter.submitList(spreads)
            binding.emptyStateGroup.isVisible = spreads.isEmpty()
        }
    }
    
    private fun setupButtons() {
        binding.createSpreadButton.setOnClickListener {
            findNavController().navigate(
                CustomSpreadManagementFragmentDirections.actionToCreateSpread()
            )
        }
    }
    
    private fun observeViewModel() {
        viewModel.deleteResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(
                    binding.root,
                    R.string.spread_deleted_successfully,
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.spread_delete_failed,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun showDeleteConfirmationDialog(spread: Spread) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_spread)
            .setMessage(getString(R.string.delete_spread_confirmation, spread.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteSpread(spread.id)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.loadCustomSpreads()
    }
}

class CustomSpreadManagementViewModel : ViewModel() {
    private val spreadRepository = SpreadRepository()
    
    private val _customSpreads = MutableLiveData<List<Spread>>()
    val customSpreads: LiveData<List<Spread>> = _customSpreads
    
    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> = _deleteResult
    
    init {
        loadCustomSpreads()
    }
    
    fun loadCustomSpreads() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            val spreads = spreadRepository.getCustomSpreadsByUserId(userId)
            _customSpreads.value = spreads
        }
    }
    
    fun deleteSpread(spreadId: Long) {
        viewModelScope.launch {
            val success = spreadRepository.deleteSpread(spreadId)
            _deleteResult.value = success
            
            if (success) {
                loadCustomSpreads()
            }
        }
    }
}

class CustomSpreadAdapter(
    private val onSpreadClick: (Spread) -> Unit,
    private val onEditClick: (Spread) -> Unit,
    private val onDeleteClick: (Spread) -> Unit
) : ListAdapter<Spread, CustomSpreadViewHolder>(SpreadDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomSpreadViewHolder {
        val binding = ItemCustomSpreadBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomSpreadViewHolder(binding, onSpreadClick, onEditClick, onDeleteClick)
    }
    
    override fun onBindViewHolder(holder: CustomSpreadViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CustomSpreadViewHolder(
    private val binding: ItemCustomSpreadBinding,
    private val onSpreadClick: (Spread) -> Unit,
    private val onEditClick: (Spread) -> Unit,
    private val onDeleteClick: (Spread) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(spread: Spread) {
        binding.spreadName.text = spread.name
        binding.cardCount.text = binding.root.context.getString(
            R.string.card_count,
            spread.positions.size
        )
        binding.spreadDescription.text = spread.description.ifEmpty {
            binding.root.context.getString(R.string.no_description)
        }
        
        binding.root.setOnClickListener {
            onSpreadClick(spread)
        }
        
        binding.editButton.setOnClickListener {
            onEditClick(spread)
        }
        
        binding.deleteButton.setOnClickListener {
            onDeleteClick(spread)
        }
    }
}

class SpreadDiffCallback : DiffUtil.ItemCallback<Spread>() {
    override fun areItemsTheSame(oldItem: Spread, newItem: Spread): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Spread, newItem: Spread): Boolean {
        return oldItem == newItem
    }
}
```

### 7. Spread Preview Component

```kotlin
class SpreadPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private var spread: Spread? = null
    private val cardRects = mutableListOf<RectF>()
    
    private val cardPaint = Paint().apply {
        color = Color.GRAY
        alpha = 100
        style = Paint.Style.FILL
    }
    
    private val cardOutlinePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = dpToPx(12f)
        textAlign = Paint.Align.CENTER
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }
    
    fun setSpread(spread: Spread) {
        this.spread = spread
        calculateCardPositions()
        invalidate()
    }
    
    private fun calculateCardPositions() {
        cardRects.clear()
        
        val spread = this.spread ?: return
        
        // Calculate card size based on view size and number of cards
        val cardWidth = width * 0.15f
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
        
        val spread = this.spread ?: return
        
        for (i in spread.positions.indices) {
            val position = spread.positions[i]
            val rect = cardRects.getOrNull(i) ?: continue
            
            // Save canvas state before rotation
            canvas.save()
            
            // Rotate canvas around position center
            canvas.rotate(
                position.rotation,
                position.xPosition * width,
                position.yPosition * height
            )
            
            // Draw card
            canvas.drawRoundRect(rect, 8f, 8f, cardPaint)
            canvas.drawRoundRect(rect, 8f, 8f, cardOutlinePaint)
            
            // Draw position number
            canvas.drawText(
                (i + 1).toString(),
                rect.centerX(),
                rect.centerY(),
                textPaint
            )
            
            // Restore canvas to original state
            canvas.restore()
        }
    }
    
    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}
```

## User Experience Enhancements

### 1. Drag and Drop Gestures

```kotlin
class DragGestureDetector(
    private val listener: OnDragGestureListener
) {
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false
    
    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = event.getPointerId(0)
                lastTouchX = event.x
                lastTouchY = event.y
                
                listener.onDragStart(lastTouchX, lastTouchY)
                isDragging = true
                return true
            }
            
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerId)
                if (pointerIndex < 0) return false
                
                val currentX = event.getX(pointerIndex)
                val currentY = event.getY(pointerIndex)
                
                if (isDragging) {
                    val dx = currentX - lastTouchX
                    val dy = currentY - lastTouchY
                    
                    listener.onDrag(dx, dy, currentX, currentY)
                }
                
                lastTouchX = currentX
                lastTouchY = currentY
                return true
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    listener.onDragEnd(lastTouchX, lastTouchY)
                    isDragging = false
                }
                
                activePointerId = MotionEvent.INVALID_POINTER_ID
                return true
            }
            
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                
                if (pointerId == activePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = event.getX(newPointerIndex)
                    lastTouchY = event.getY(newPointerIndex)
                    activePointerId = event.getPointerId(newPointerIndex)
                }
                return true
            }
        }
        
        return false
    }
    
    interface OnDragGestureListener {
        fun onDragStart(x: Float, y: Float)
        fun onDrag(dx: Float, dy: Float, x: Float, y: Float)
        fun onDragEnd(x: Float, y: Float)
    }
}
```

### 2. Rotation Gesture Detector

```kotlin
class RotationGestureDetector(
    private val listener: OnRotationGestureListener
) {
    private var focalX = 0f
    private var focalY = 0f
    private var prevAngle = 0f
    private var isRotating = false
    
    fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount != 2) {
            isRotating = false
            return false
        }
        
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    // Calculate the focal point
                    focalX = (event.getX(0) + event.getX(1)) / 2
                    focalY = (event.getY(0) + event.getY(1)) / 2
                    
                    // Calculate the initial angle
                    prevAngle = calculateAngle(event)
                    
                    listener.onRotationBegin(focalX, focalY)
                    isRotating = true
                    return true
                }
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (isRotating && event.pointerCount == 2) {
                    // Calculate current angle
                    val currentAngle = calculateAngle(event)
                    
                    // Calculate the angle difference
                    var angleDiff = currentAngle - prevAngle
                    
                    // Normalize the angle difference
                    if (angleDiff > 180) {
                        angleDiff -= 360
                    } else if (angleDiff < -180) {
                        angleDiff += 360
                    }
                    
                    // Update the focal point
                    focalX = (event.getX(0) + event.getX(1)) / 2
                    focalY = (event.getY(0) + event.getY(1)) / 2
                    
                    listener.onRotate(angleDiff, focalX, focalY)
                    
                    prevAngle = currentAngle
                    return true
                }
            }
            
            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isRotating) {
                    listener.onRotationEnd(focalX, focalY)
                    isRotating = false
                    return true
                }
            }
        }
        
        return false
    }
    
    private fun calculateAngle(event: MotionEvent): Float {
        val dx = event.getX(0) - event.getX(1)
        val dy = event.getY(0) - event.getY(1)
        
        // Calculate angle in degrees
        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        
        // Normalize to [0, 360)
        if (angle < 0) {
            angle += 360f
        }
        
        return angle
    }
    
    interface OnRotationGestureListener {
        fun onRotationBegin(focalX: Float, focalY: Float)
        fun onRotate(angle: Float, focalX: Float, focalY: Float)
        fun onRotationEnd(focalX: Float, focalY: Float)
    }
}
```

### 3. Position Snapping

```kotlin
class PositionSnapHelper {
    private val snapThreshold = 0.05f // 5% of view dimension
    
    fun snapPosition(
        position: SpreadPosition,
        allPositions: List<SpreadPosition>,
        viewWidth: Float,
        viewHeight: Float
    ): SpreadPosition {
        // Skip if there are no other positions to snap to
        if (allPositions.size <= 1) return position
        
        val posX = position.xPosition
        val posY = position.yPosition
        
        // Check for grid snapping
        val snappedToGrid = snapToGrid(posX, posY)
        
        // If already snapped to grid, return
        if (snappedToGrid.first != posX || snappedToGrid.second != posY) {
            return position.copy(
                xPosition = snappedToGrid.first,
                yPosition = snappedToGrid.second
            )
        }
        
        // Check for alignment with other positions
        var bestSnapX = posX
        var bestSnapY = posY
        var minDistanceX = snapThreshold
        var minDistanceY = snapThreshold
        
        for (otherPos in allPositions) {
            if (otherPos.id == position.id) continue
            
            // Check horizontal alignment
            val distanceY = abs(posY - otherPos.yPosition)
            if (distanceY < minDistanceY) {
                minDistanceY = distanceY
                bestSnapY = otherPos.yPosition
            }
            
            // Check vertical alignment
            val distanceX = abs(posX - otherPos.xPosition)
            if (distanceX < minDistanceX) {
                minDistanceX = distanceX
                bestSnapX = otherPos.xPosition
            }
        }
        
        return position.copy(
            xPosition = if (minDistanceX < snapThreshold) bestSnapX else posX,
            yPosition = if (minDistanceY < snapThreshold) bestSnapY else posY
        )
    }
    
    private fun snapToGrid(x: Float, y: Float): Pair<Float, Float> {
        // Snap to 10% grid
        val gridSize = 0.1f
        
        val snappedX = round(x / gridSize) * gridSize
        val snappedY = round(y / gridSize) * gridSize
        
        return Pair(snappedX, snappedY)
    }
}
```

### 4. Visual Feedback

```kotlin
class VisualFeedbackManager(private val context: Context) {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    
    fun provideSelectionFeedback() {
        // Haptic feedback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(30)
        }
    }
    
    fun provideSnapFeedback() {
        // Haptic feedback for snapping
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }
    }
    
    fun provideRotationFeedback() {
        // Subtle haptic feedback for rotation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }
    
    fun showPositionAddedAnimation(view: View, x: Float, y: Float) {
        // Create a ripple effect
        val ripple = RippleDrawable(
            ColorStateList.valueOf(Color.WHITE),
            null,
            null
        )
        
        // Create a temporary view for the animation
        val rippleView = View(context).apply {
            background = ripple
            alpha = 0.7f
            scaleX = 0f
            scaleY = 0f
            translationX = x - view.width / 2
            translationY = y - view.height / 2
            layoutParams = FrameLayout.LayoutParams(100, 100)
        }
        
        // Add to parent view
        (view.parent as? ViewGroup)?.addView(rippleView)
        
        // Animate the ripple
        rippleView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                (rippleView.parent as? ViewGroup)?.removeView(rippleView)
            }
            .start()
    }
    
    fun showPositionDeletedAnimation(view: View, position: SpreadPosition) {
        // Calculate position in view coordinates
        val x = position.xPosition * view.width
        val y = position.yPosition * view.height
        
        // Create a fading animation
        val fadeView = View(context).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.RED)
            }
            alpha = 0.7f
            scaleX = 1f
            scaleY = 1f
            translationX = x - 50
            translationY = y - 50
            layoutParams = FrameLayout.LayoutParams(100, 100)
        }
        
        // Add to parent view
        (view.parent as? ViewGroup)?.addView(fadeView)
        
        // Animate the fade
        fadeView.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                (fadeView.parent as? ViewGroup)?.removeView(fadeView)
            }
            .start()
    }
}
```

## Template Spreads

```kotlin
object SpreadTemplates {
    fun getTemplates(): List<Spread> {
        return listOf(
            createThreeCardSpread(),
            createCelticCrossSpread(),
            createRelationshipSpread(),
            createCareerPathSpread(),
            createSpiritualGuidanceSpread()
        )
    }
    
    private fun createThreeCardSpread(): Spread {
        return Spread(
            id = 1,
            name = "Three Card Spread",
            description = "A simple but powerful spread for quick insights into past, present, and future.",
            isCustom = false,
            isDefault = true,
            isPremium = false,
            userId = null,
            createdAt = System.currentTimeMillis(),
            positions = listOf(
                SpreadPosition(
                    id = 1,
                    positionNumber = 1,
                    name = "Past",
                    description = "Influences from the past that are affecting the current situation.",
                    xPosition = 0.3f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 2,
                    positionNumber = 2,
                    name = "Present",
                    description = "The current situation and immediate influences.",
                    xPosition = 0.5f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 3,
                    positionNumber = 3,
                    name = "Future",
                    description = "Potential outcomes and future influences.",
                    xPosition = 0.7f,
                    yPosition = 0.5f,
                    rotation = 0f
                )
            )
        )
    }
    
    private fun createCelticCrossSpread(): Spread {
        return Spread(
            id = 2,
            name = "Celtic Cross",
            description = "A comprehensive 10-card spread that provides deep insight into a specific question or situation.",
            isCustom = false,
            isDefault = true,
            isPremium = false,
            userId = null,
            createdAt = System.currentTimeMillis(),
            positions = listOf(
                SpreadPosition(
                    id = 1,
                    positionNumber = 1,
                    name = "Present",
                    description = "The present situation or influence.",
                    xPosition = 0.4f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 2,
                    positionNumber = 2,
                    name = "Challenge",
                    description = "The immediate challenge or obstacle.",
                    xPosition = 0.4f,
                    yPosition = 0.5f,
                    rotation = 90f
                ),
                SpreadPosition(
                    id = 3,
                    positionNumber = 3,
                    name = "Past",
                    description = "Past events or influences relevant to the question.",
                    xPosition = 0.3f,
                    yPosition = 0.35f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 4,
                    positionNumber = 4,
                    name = "Future",
                    description = "Future events or influences that may come into play.",
                    xPosition = 0.5f,
                    yPosition = 0.35f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 5,
                    positionNumber = 5,
                    name = "Above",
                    description = "Your conscious thoughts, goals, and ideals.",
                    xPosition = 0.4f,
                    yPosition = 0.2f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 6,
                    positionNumber = 6,
                    name = "Below",
                    description = "Your unconscious influences, hidden factors, or repressed feelings.",
                    xPosition = 0.4f,
                    yPosition = 0.65f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 7,
                    positionNumber = 7,
                    name = "Advice",
                    description = "Guidance or advice to consider.",
                    xPosition = 0.7f,
                    yPosition = 0.2f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 8,
                    positionNumber = 8,
                    name = "External Influences",
                    description = "How others see you or external factors affecting the situation.",
                    xPosition = 0.7f,
                    yPosition = 0.35f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 9,
                    positionNumber = 9,
                    name = "Hopes or Fears",
                    description = "Your hopes and/or fears regarding the outcome.",
                    xPosition = 0.7f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 10,
                    positionNumber = 10,
                    name = "Outcome",
                    description = "The potential outcome if the current course is followed.",
                    xPosition = 0.7f,
                    yPosition = 0.65f,
                    rotation = 0f
                )
            )
        )
    }
    
    private fun createRelationshipSpread(): Spread {
        return Spread(
            id = 3,
            name = "Relationship Spread",
            description = "A 7-card spread designed to explore the dynamics of a relationship.",
            isCustom = false,
            isDefault = true,
            isPremium = false,
            userId = null,
            createdAt = System.currentTimeMillis(),
            positions = listOf(
                SpreadPosition(
                    id = 1,
                    positionNumber = 1,
                    name = "You",
                    description = "Your role and perspective in the relationship.",
                    xPosition = 0.3f,
                    yPosition = 0.3f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 2,
                    positionNumber = 2,
                    name = "Partner",
                    description = "Your partner's role and perspective in the relationship.",
                    xPosition = 0.7f,
                    yPosition = 0.3f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 3,
                    positionNumber = 3,
                    name = "Connection",
                    description = "The nature of your connection and what brings you together.",
                    xPosition = 0.5f,
                    yPosition = 0.3f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 4,
                    positionNumber = 4,
                    name = "Strengths",
                    description = "The strengths of the relationship.",
                    xPosition = 0.4f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 5,
                    positionNumber = 5,
                    name = "Challenges",
                    description = "The challenges or obstacles in the relationship.",
                    xPosition = 0.6f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 6,
                    positionNumber = 6,
                    name = "Potential",
                    description = "The potential or direction of the relationship.",
                    xPosition = 0.5f,
                    yPosition = 0.7f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 7,
                    positionNumber = 7,
                    name = "Advice",
                    description = "Guidance for nurturing the relationship.",
                    xPosition = 0.5f,
                    yPosition = 0.5f,
                    rotation = 0f
                )
            )
        )
    }
    
    private fun createCareerPathSpread(): Spread {
        return Spread(
            id = 4,
            name = "Career Path Spread",
            description = "A 5-card spread focused on career decisions and professional growth.",
            isCustom = false,
            isDefault = true,
            isPremium = true,
            userId = null,
            createdAt = System.currentTimeMillis(),
            positions = listOf(
                SpreadPosition(
                    id = 1,
                    positionNumber = 1,
                    name = "Current Position",
                    description = "Your current career situation or mindset.",
                    xPosition = 0.5f,
                    yPosition = 0.3f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 2,
                    positionNumber = 2,
                    name = "Strengths",
                    description = "Your professional strengths and assets.",
                    xPosition = 0.3f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 3,
                    positionNumber = 3,
                    name = "Challenges",
                    description = "Obstacles or areas for growth in your career.",
                    xPosition = 0.7f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 4,
                    positionNumber = 4,
                    name = "Action",
                    description = "Steps to take for professional advancement.",
                    xPosition = 0.4f,
                    yPosition = 0.7f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 5,
                    positionNumber = 5,
                    name = "Outcome",
                    description = "Potential outcome or direction of your career path.",
                    xPosition = 0.6f,
                    yPosition = 0.7f,
                    rotation = 0f
                )
            )
        )
    }
    
    private fun createSpiritualGuidanceSpread(): Spread {
        return Spread(
            id = 5,
            name = "Spiritual Guidance Spread",
            description = "A 6-card spread for spiritual insight and personal growth.",
            isCustom = false,
            isDefault = true,
            isPremium = true,
            userId = null,
            createdAt = System.currentTimeMillis(),
            positions = listOf(
                SpreadPosition(
                    id = 1,
                    positionNumber = 1,
                    name = "Current Spiritual State",
                    description = "Your current spiritual or energetic condition.",
                    xPosition = 0.5f,
                    yPosition = 0.3f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 2,
                    positionNumber = 2,
                    name = "Lesson to Learn",
                    description = "A spiritual lesson or insight to focus on.",
                    xPosition = 0.3f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 3,
                    positionNumber = 3,
                    name = "Spiritual Challenge",
                    description = "An obstacle to overcome on your spiritual path.",
                    xPosition = 0.7f,
                    yPosition = 0.5f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 4,
                    positionNumber = 4,
                    name = "Guidance",
                    description = "Spiritual guidance or direction to consider.",
                    xPosition = 0.4f,
                    yPosition = 0.7f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 5,
                    positionNumber = 5,
                    name = "Hidden Wisdom",
                    description = "Deeper wisdom or insight that may not be immediately apparent.",
                    xPosition = 0.6f,
                    yPosition = 0.7f,
                    rotation = 0f
                ),
                SpreadPosition(
                    id = 6,
                    positionNumber = 6,
                    name = "Integration",
                    description = "How to integrate these insights into your daily life.",
                    xPosition = 0.5f,
                    yPosition = 0.5f,
                    rotation = 0f
                )
            )
        )
    }
}
```